import networkx as nx


class GraphGener():

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
