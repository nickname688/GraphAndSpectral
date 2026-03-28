import numpy as np
import networkx as nx
import random as r
import math
from PyQt5.QtCore import QThread, pyqtSignal


# 求解固定边数的谱极值问题
class SolverFixOrder(QThread):
    # 自定义信号声明
    # 使用自定义信号和UI主线程通讯，参数是发送信号时附带参数的数据类型，可以是str、int、list等
    finishSignal = pyqtSignal(str)
    graphSignal = pyqtSignal(type(nx.Graph()))

    k4 = None
    k2_3 = None

    ifPlanar = False
    ifOutPlanar = False
    ifTuran = False

    # 带一个参数t
    def __init__(self, times, order, k1, banGraph, ifPlanar=False, ifOutPlanar=False, parent=None,
                 ifMultCore=False, ifTuran=False):
        super(SolverFixOrder, self).__init__(parent)

        self.times = times
        self.order = order
        self.k1 = k1
        self.banGraph = banGraph
        self.ifTuran = ifTuran
        self.ifMultCore = ifMultCore

        tp = [[0, 1, 1, 1],
              [1, 0, 1, 1],
              [1, 1, 0, 1],
              [1, 1, 1, 0]]

        self.k4 = nx.Graph(np.matrix(tp))

        tp = [[0, 0, 1, 1, 1],
              [0, 0, 1, 1, 1],
              [1, 1, 0, 0, 0],
              [1, 1, 0, 0, 0],
              [1, 1, 0, 0, 0]]

        self.k2_3 = nx.Graph(np.matrix(tp))

        self.ifPlanar = ifPlanar
        self.ifOutPlanar = ifOutPlanar

    # run函数是子线程中的操作，线程启动后开始执行
    def run(self):
        ansSp, ansGraph = self.GetExternalGraph(self.order, self.banGraph, self.times, self.k1, ifPlanar=self.ifPlanar,
                                                ifOutPlanar=self.ifOutPlanar)

        isa = list(nx.isolates(ansGraph))
        ansGraph.remove_nodes_from(isa)

        self.graphSignal.emit(ansGraph)

        self.finishSignal.emit(str(ansSp))

        return

    # 生成初始图
    # n的点没有边的图
    def GenStart(self, n, ban, ifPlanar=False, ifOutPlanar=False):
        t = nx.Graph()
        for i in range(0, n):
            t.add_node(i)

        flag = True

        while flag:
            t, flag = self.AddEdgeStart(t, ban, ifPlanar, ifOutPlanar)

        return t

    def AddEdgeStart(self, t, ban, ifPlanar, ifOutPlanar):
        node1 = [i for i in range(t.order())]
        # print(node1)

        node1 = sorted(node1, key=lambda x: t.degree(x), reverse=True)

        # node1 = [i for i in range(t.order())]
        # r.shuffle(node1)

        for selectNode1 in node1:

            node2 = self.FindNonAdj(t, selectNode1)
            r.shuffle(node2)

            for selectNode2 in node2:

                te = t.copy()

                te.add_edge(selectNode1, selectNode2)

                if ifPlanar:
                    if not nx.check_planarity(te)[0]:
                        continue

                if ifOutPlanar:
                    if not self.CheckGraphPlanar(np.matrix(nx.adjacency_matrix(te).todense())):
                        continue

                if ban != None and nx.isomorphism.GraphMatcher(te, ban).subgraph_is_monomorphic():
                    continue

                return te, True

        return t, False

    def AddEdge(self, t, ban, ifPlanar, ifOutPlanar):
        node1 = [i for i in range(t.order())]
        r.shuffle(node1)

        for selectNode1 in node1:

            node2 = self.FindNonAdj(t, selectNode1)
            r.shuffle(node2)

            for selectNode2 in node2:

                te = t.copy()

                te.add_edge(selectNode1, selectNode2)

                if ifPlanar:
                    if not nx.check_planarity(te)[0]:
                        continue

                if ifOutPlanar:
                    if not self.CheckGraphPlanar(np.matrix(nx.adjacency_matrix(te).todense())):
                        continue

                if ban != None and nx.isomorphism.GraphMatcher(te, ban).subgraph_is_monomorphic():
                    continue

                return te, True

        return t, False

    # 查找图中所有度顶点集
    def FindDegreeM0(self, g):
        ord = g.order()
        res = [i for i in range(ord)]

        return res

    # 查找图g中所有与i不相邻的顶点集
    def FindNonAdj(self, g, j):
        ord = g.order()
        a = list(g.neighbors(j))
        res = [i for i in range(ord) if i not in a and i != j]

        return res

    # 检测图是否连通且可行
    def CheckAvail(self, te, ban, ifPlanar=False, ifOutPlanar=False):
        if nx.isomorphism.GraphMatcher(te, ban).subgraph_is_monomorphic():
            return False

        ord = te.order()

        for i in list(nx.connected_components(te)):
            sub_graph = te.subgraph(i)
            if sub_graph.size() == ord - 1:
                return True

        return False

    def GetGraphEdges(self, g):
        res = []
        for i in range(len(g)):
            for j in range(i + 1, len(g)):
                if g[i, j] == 1:
                    # if True:
                    res.append([i, j])
        return res

    # 对边进行minor处理
    def MinorEdge(self, g, pair):
        a = min(pair[0], pair[1])
        b = max(pair[0], pair[1])

        g1 = np.delete(g, pair, axis=1)
        g1 = np.delete(g1, pair, axis=0)

        index = list(range(len(g)))
        index.pop(b)
        index.pop(a)
        newList = [1 if (g[i, a] + g[i, b] >= 1) else 0 for i in index]

        newColumn = np.array(newList)  # 新列

        newList.append(0)
        newRow = np.array(newList)  # 新行

        newMatrix = np.insert(g1, len(g) - 2, newColumn, axis=1)
        newMatrix = np.insert(newMatrix, len(g) - 2, newRow, axis=0)

        return newMatrix

    # 传入一个图（n x n矩阵），检测是否是外平面图
    def CheckGraphPlanar(self, g):
        if len(g) <= 3:
            return True

        if len(g) == 4:
            if nx.isomorphism.GraphMatcher(nx.Graph(g), self.k4).subgraph_is_monomorphic():
                return False
            else:
                return True
        elif len(g) == 5:
            if nx.isomorphism.GraphMatcher(nx.Graph(g), self.k2_3).subgraph_is_monomorphic():
                return False

        if nx.isomorphism.GraphMatcher(nx.Graph(g), self.k4).subgraph_is_monomorphic():
            return False
        if nx.isomorphism.GraphMatcher(nx.Graph(g), self.k2_3).subgraph_is_monomorphic():
            return False

        pairs = self.GetGraphEdges(g)
        newGraphs = [self.MinorEdge(g, pair) for pair in pairs]

        # newGraphs = DeleteMonomorphic()

        ans = [self.CheckGraphPlanar(newGraph) for newGraph in newGraphs]
        if False in ans:
            return False

        return True

    # 检测是否继续删除边
    def CheckIfDelete(self, graph, maxalEdge):
        rate = 0.8 / maxalEdge * graph.number_of_edges()
        # print(rate, graph.number_of_edges())
        return r.random() < rate

    # 获取禁用边数极图
    def GetExternalGraph(self, m, ban, time, k1, ifPlanar=False, ifOutPlanar=False):
        tempture = 100
        rate = 0.95
        endTemp = 0.01

        # 记录上一步的解
        oldSp = 0
        oldGraph = None

        # 记录最优解
        ansSp = 0
        ansGraph = None

        gra = self.GenStart(m, ban=ban, ifPlanar=ifPlanar, ifOutPlanar=ifOutPlanar)
        maxalEdge = gra.number_of_edges()
        oldGraph = gra

        rollRate = 0

        for times in range(time):

            tper = int(round(times / time, 2) * 100)
            if tper != rollRate:
                rollRate = tper
                self.finishSignal.emit(str(tper) + '%')

            if times % 10 == 0:
                print(end=' ')
                print(times, end='')

            if tempture < endTemp:
                break

            # 找新的解
            # while(True):

            te = oldGraph.copy()

            # 删边
            selectEdge = r.choice(([i[0] for i in te.edges.items()]))
            te.remove_edge(selectEdge[0], selectEdge[1])

            while self.CheckIfDelete(te, maxalEdge):
                selectEdge = r.choice(([i[0] for i in te.edges.items()]))
                te.remove_edge(selectEdge[0], selectEdge[1])

            # 增边
            flag = True

            while flag:
                te, flag = self.AddEdge(te, ban, ifPlanar, ifOutPlanar)

            new = te

            adjMatrix = np.matrix(nx.adjacency_matrix(new).todense())

            if self.ifTuran:
                res = new.number_of_edges()
            else:
                res = max(np.linalg.eig(adjMatrix)[0])

            # 替换最优解
            if ansSp < res:
                print('  NewAnswer!', end='')
                print(times, end='')
                ansSp = res
                ansGraph = te

            if oldSp < res:
                oldSp = res
                oldGraph = te
                tempture *= rate
            else:
                para = np.real((res - oldSp) / (k1 * tempture))
                pro = math.exp(para)
                if r.random() < pro:
                    oldSp = res
                    oldGraph = te
                    tempture *= rate
                else:
                    pass

        print('\nEnd!')
        print('endTemp:' + str(tempture))

        return ansSp, ansGraph
