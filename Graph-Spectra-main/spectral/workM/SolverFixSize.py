import numpy as np
import networkx as nx
import random as r
import math
from PyQt5.QtCore import QThread, pyqtSignal



def MultCalu(solver, para):
    p0 = para[0]
    p1 = para[1]
    p2 = para[2]
    p3 = para[3]
    p4 = para[4]
    p5 = para[5]
    return solver.GetExternalGraph(p0, p1, p2, p3, p4, p5)


# 求解固定边数的谱极值问题
class SolverFixSize(QThread):
    # 自定义信号声明
    # 使用自定义信号和UI主线程通讯，参数是发送信号时附带参数的数据类型，可以是str、int、list等
    finishSignal = pyqtSignal(str)
    graphSignal = pyqtSignal(type(nx.Graph()))

    k4 = None
    k2_3 = None

    ifPlanar = False
    ifOutPlanar = False

    ansList = []
    ansGraList = []

    # 带一个参数t
    def __init__(self, times, order, k1, banGraph, ifPlanar=False, ifOutPlanar=False,
                 ifMultCore=False, parent=None):
        super(SolverFixSize, self).__init__(parent)

        self.times = times
        self.order = order
        self.k1 = k1
        self.banGraph = banGraph


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

        para = [self.order, self.banGraph, self.times, self.k1,
                self.ifPlanar, self.ifOutPlanar]

        ansSp, ansGraph = MultCalu(self, para)

        # ansSp, ansGraph = self.GetExternalGraph(self.order, self.banGraph, self.times, self.k1,
        #                                         ifPlanar=self.ifPlanar, ifOutPlanar=self.ifOutPlanar)

        isa = list(nx.isolates(ansGraph))
        ansGraph.remove_nodes_from(isa)

        # 传递结果到主窗口中
        self.graphSignal.emit(ansGraph)

        self.finishSignal.emit(str(ansSp))

        return

        #
        # for i in range(self.t):
        #     time1.sleep(0.5)
        #     # 发射自定义信号
        #     # 通过emit函数将参数i传递给主线程，触发自定义信号
        #     self.finishSignal.emit(str(i))  # 注意这里与_signal = pyqtSignal(str)中的类型相同

    # 生成初始图
    def GenStart(self, m):
        t = nx.Graph()
        for i in range(0, m):
            t.add_edge(i, i + 1)

        return t

    # 查找图中所有度大于1的顶点集
    def FindDegreeM1(self, g):
        ord = g.order()
        res = [i for i in range(ord) if g.degree(i) > 0]

        return res

    # 查找图g中所有与i不相邻的顶点集
    def FindNonAdj(self, g, j):
        ord = g.order()
        a = list(g.neighbors(j))
        res = [i for i in range(ord) if i not in a and i != j]

        return res

    # 检测图是否连通且可行
    def CheckAvail(self, te, ban, ifPlanar=False, ifOutPlanar=False):
        if ban != None and nx.isomorphism.GraphMatcher(te, ban).subgraph_is_monomorphic():
            return False

        ord = te.order()

        for i in list(nx.connected_components(te)):
            sub_graph = te.subgraph(i)
            if sub_graph.size() == ord - 1:

                # 检测平面性
                if ifPlanar:
                    if not nx.check_planarity(te)[0]:
                        return False
                if ifOutPlanar:
                    if not self.CheckGraphPlanar(np.matrix(nx.adjacency_matrix(te).todense())):
                        return False

                return True

        return False

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

    def GetGraphEdges(self, g):
        res = []
        for i in range(len(g)):
            for j in range(i + 1, len(g)):
                if g[i, j] == 1:
                    # if True:
                    res.append([i, j])
        return res

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

    # 获取禁用边数极图
    # m 边数 ban禁用子图 time最大循环次数 k1 概率修正 e外部窗体
    def GetExternalGraph(self, m, ban, time, k1, ifPlanar=False, ifOutPlanar=False):
        # _thread.start_new_thread(ProgressBar, (e, time))
        tempture = 100
        rate = 0.985
        endTemp = 0.001

        # 记录上一步的解
        oldSp = 0
        oldGraph = None

        # 记录最优解
        ansSp = 0
        ansGraph = None

        gra = self.GenStart(m)
        oldGraph = gra

        rollRate = 0

        for times in range(time):

            tper = int(round(times / time, 2) * 100)
            if tper != rollRate:
                rollRate = tper
                self.finishSignal.emit(str(tper) + '%')

            if tempture < endTemp:
                break

            # 找新的解
            while True:

                te = oldGraph.copy()

                selectEdge = r.choice(([i[0] for i in te.edges.items()]))
                te.remove_edge(selectEdge[0], selectEdge[1])
                node1 = self.FindDegreeM1(te)

                selectNode1 = r.choice(node1)
                node2 = self.FindNonAdj(te, selectNode1)

                selectNode2 = r.choice(node2)

                te.add_edge(selectNode1, selectNode2)

                if self.CheckAvail(te, ban, ifPlanar=self.ifPlanar,
                                   ifOutPlanar=self.ifOutPlanar):
                    new = te
                    break

            adjMatrix = np.matrix(nx.adjacency_matrix(new).todense())
            res = max(np.linalg.eig(adjMatrix)[0])

            # 替换最优解
            if ansSp < res:
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
