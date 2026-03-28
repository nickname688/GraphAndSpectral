import networkx as nx


def CheckNumOrChar(a):
    if a.isdigit():  # or a.str_1.isalpha():
        return True
    else:
        return False


def CheckAndTrans(a):
    if a.count('-') > 1 or a[0] == '-' or a[-1] == '-':
        return None

    if '-' not in a:
        return [a]

    else:
        t = a.split('-')
        s1 = t[0]
        s2 = t[1]

        s1 = int(s1)
        s2 = int(s2)

        if s2 - s1 < 0 or s2 - s1 >= 50:
            return None

        ans_ = list(str(i) for i in range(s1, s2 + 1))
        return ans_


class GraphGener:

    def __init__(self):
        pass

    def GetGraph(self, name):
        if name == "C3/K3":
            return self.GetC3()
        elif name == "C4":
            return self.GetC4()
        elif name == "K4":
            return self.GetK4()
        elif name == "C5":
            return self.GetC5()
        elif name == "M5":
            return self.GetM5()

    def GetC3(self):
        gra = nx.Graph()
        gra.add_edge('1', '2')
        gra.add_edge('2', '3')
        gra.add_edge('1', '3')

        return gra

    def GetC4(self):
        gra = nx.Graph()
        gra.add_edge('1', '2')
        gra.add_edge('2', '3')
        gra.add_edge('3', '4')
        gra.add_edge('1', '4')

        return gra

    def GetK4(self):
        gra = nx.Graph()
        for i in range(1, 5):
            for j in range(i + 1, 5):
                gra.add_edge(str(i), str(j))

        return gra

    def GetC5(self):
        gra = nx.Graph()
        gra.add_edge('1', '2')
        gra.add_edge('2', '3')
        gra.add_edge('3', '4')
        gra.add_edge('4', '5')
        gra.add_edge('5', '1')

        return gra

    def GetM5(self):
        gra = self.GetC4()
        for i in range(1, 5):
            gra.add_edge('5', str(i))

        return gra

    def GetGraphByCommand(self, gr, str_):

        gra = gr
        if gr is None:
            gra = nx.Graph()

        a = [[], []]
        aim = 0
        flag = False
        flagChanged = False

        for i in str_:
            if i == '(' or i == '（':
                flag = True

            elif i == ')' or i == '）':
                flag = False
                if aim == 0:
                    aim = 1
                else:
                    aim = 0
                    gra = self.Link_(gra, ''.join(a[0]), ''.join(a[1]))
                    flagChanged = True
                    if gra is None:
                        return None
                    a = [[], []]

            elif CheckNumOrChar(i) or i == '-':

                if flag:
                    a[aim].append(i)
                    continue
                else:
                    a[aim].append(i)
                    if aim == 0:
                        aim = 1
                    else:
                        aim = 0
                        gra = self.Link_(gra, ''.join(a[0]), ''.join(a[1]))
                        flagChanged = True
                        if gra is None:
                            return None
                        a = [[], []]
            else:
                return None

        return gra if flagChanged else None

    # 检测合法性
    # -数量不能超过一个
    # -不能出现在错误的位置
    def Link_(self, g, a, b):
        a = CheckAndTrans(a)
        b = CheckAndTrans(b)

        if a is None or b is None:
            return None

        for i in a:
            for j in b:
                if i == j:
                    continue
                g.add_edge(i, j)

        return g
