import sys
from PyQt5.QtWidgets import QApplication, QMainWindow, QMessageBox, QGraphicsPixmapItem, QGraphicsScene
from PyQt5.QtGui import QImage, QPixmap

import workM.ui.UIstart as start
from workM.GraphGener import *

from functools import partial
import numpy as np
import networkx as nx
import matplotlib.pyplot as plt
import io
import PIL
from PIL import Image


class MainWindow(QMainWindow, start.Ui_MainWindow):
    settingWindow = None

    gr = nx.Graph()
    ansGr = None

    def __init__(self, parent=None):
        super(MainWindow, self).__init__(parent)
        self.thread = None
        self.setupUi(self)

        self.addEdgeBtn.clicked.connect(partial(self.AddEdge, self, self))
        self.delEdgeBtn.clicked.connect(partial(self.DelEdge, self, self))
        self.delVerBtn.clicked.connect(partial(self.DelVertex, self, self))

        self.freshforbidGraphBtn.clicked.connect(partial(self.freshForbidGraph, self))

        self.forBidGraphSelectComboBox.currentIndexChanged.connect(partial(self.SelectChanged))

        self.autoComputeBtn.clicked.connect(partial(self.AutoComputeChanged, ))

        self.startComputeBtn.clicked.connect(partial(self.ComputeStart, ))  # 开始计算

        self.clearBtn.clicked.connect(partial(self.ClearGraph, ))  # 清空

        self.commandBtn.clicked.connect(partial(self.CommandGraph, ))  # 确定指令

    def CommandGraph(self):
        graphGener = GraphGener()
        text = self.commandLine.text()

        gr1 = None

        gr1 = graphGener.GetGraphByCommand(self.gr, text)

        if gr1 != None:
            self.gr = gr1
            self.freshForbidGraph(self)
            if self.autoComputeBtn.isChecked():
                self.Compute()
        else:
            QMessageBox.critical(self, "错误", "输入有误", QMessageBox.Yes, QMessageBox.Yes)

    def ClearGraph(self):
        self.gr = nx.Graph()
        self.freshForbidGraph(self)

        self.textEdit.clear()
        self.textEdit_2.clear()

    def ComputeStart(self):
        self.Compute()

    def AutoComputeChanged(self):
        if self.autoComputeBtn.isChecked():
            self.startComputeBtn.setEnabled(False)
        else:
            self.startComputeBtn.setEnabled(True)

    def FreshAnsGraph(self, gra):
        self.FreshGraph(self.ansGr, 1)

    def SetAnsGraph(self, gra):
        self.StartCalBtn.setEnabled(True)
        self.ansGr = gra
        self.FreshAnsGraph(gra)

    # 绘制禁用的图
    def freshForbidGraph(self, e):
        self.FreshGraph(self.gr, 0)

    def FreshGraph(self, graph, freshId):

        if graph == None:
            return

        aim = None

        if freshId == 0:
            aim = self.forbidGraph

        elif freshId == 1:
            aim = self.maxGraph

        sizeX = aim.size().width() / 100
        sizeY = aim.size().height() / 100

        if not (2 / 3 < sizeX / sizeY < 3 / 2):
            if sizeX > sizeY:
                sizeX = round(1.5 * sizeY)
            else:
                sizeY = round(1.5 * sizeX)

        f = plt.figure(figsize=(sizeX, sizeY))
        nx.draw(graph, with_labels=freshId == 0, node_size=100, node_color='skyblue', font_color='black',
                font_weight='bold',
                ax=f.add_subplot(111))

        buffer = io.BytesIO()  # using buffer,great way!
        # 把plt的内容保存在内存中
        plt.savefig(buffer, format='jpg')

        plt.cla()
        plt.close("all")

        dataPIL = PIL.Image.open(buffer)

        im = np.asarray(dataPIL)

        qImage = QImage(im.tobytes(), im.shape[1], im.shape[0], im.shape[1] * 3, QImage.Format_RGB888)

        pix = QPixmap.fromImage(qImage)
        item = QGraphicsPixmapItem(pix)
        scene = QGraphicsScene()
        scene.addItem(item)
        aim.setScene(scene)

    # 点击加入边
    def AddEdge(self, e, mw):
        node1 = e.addLine1.text()
        node2 = e.addLine2.text()

        if node1 == "" or node2 == "":
            QMessageBox.critical(mw, "错误", "未输入参数", QMessageBox.Yes, QMessageBox.Yes)
            return 0
        elif node1 == node2:
            QMessageBox.critical(mw, "错误", "不允许加入自环", QMessageBox.Yes, QMessageBox.Yes)
            return 0
        else:
            self.gr.add_edge(node2, node1)
            e.addLine1.setText("")
            e.addLine2.setText("")
            self.forBidGraphSelectComboBox.setCurrentIndex(1)
            self.freshForbidGraph(e)
            if self.autoComputeBtn.isChecked():
                self.Compute()

    # 点击删除边
    def DelEdge(self, e, mw):
        node1 = e.delLine1.text()
        node2 = e.delLine2.text()

        if node1 == "" or node2 == "":
            QMessageBox.critical(mw, "错误", "未输入参数")
            return 0

        flag = not ((node1, node2) in list(self.gr.edges()) or (node2, node1) in list(self.gr.edges()))

        if flag:
            QMessageBox.critical(e, "错误", "不存在边")
            return 0
        else:
            e.delLine1.setText("")
            e.delLine2.setText("")
            self.gr.remove_edge(node1, node2)
            self.forBidGraphSelectComboBox.setCurrentIndex(1)
            self.freshForbidGraph(e)
            if self.autoComputeBtn.isChecked():
                self.Compute()

    # 点击删除点
    def DelVertex(self, e, mw):
        node = e.delVerLine.text()

        if node == "":
            QMessageBox.critical(mw, "错误", "未输入参数")
            return 0

        flag = not (node in list(self.gr.nodes()))

        if flag:
            QMessageBox.critical(mw, "错误", "不存在点")
            return 0
        else:
            e.delVerLine.setText("")
            self.gr.remove_node(node)
            self.forBidGraphSelectComboBox.setCurrentIndex(1)
            self.freshForbidGraph(e)
            if self.autoComputeBtn.isChecked():
                self.Compute()

    # 选中的禁用子图发生改变
    def SelectChanged(self):
        graphText = self.forBidGraphSelectComboBox.currentText()

        if graphText == "自定义" or graphText == "选择禁用子图":
            return

        graphGener = GraphGener()

        self.gr = graphGener.GetGraph(graphText)
        self.freshForbidGraph(self)

        if self.autoComputeBtn.isChecked():
            self.Compute()

    def ChangeRoll(self, msg):
        if msg[-1] == '%':
            self.textEdit.setText(msg)
        else:
            te = str(msg + '\n')
            matrix = np.matrix(nx.adjacency_matrix(self.ansGr).todense())
            matStr = str(matrix)

            self.textEdit.setText(te + matStr)

    def Compute(self):
        graph = self.gr

        ord = graph.order()
        size = graph.number_of_edges()

        s = np.matrix(nx.adjacency_matrix(graph).todense())
        eig = np.linalg.eig(s)
        value_ = np.round(max(eig[0]), 4)
        posit = np.where(eig[0] == np.max(eig[0]))[0][0]
        flag = 1 if eig[1][0, posit] > 0 else -1

        vector = [np.real(np.round(eig[1][i, posit] * flag, 3)) for i in range(ord)]

        nodes = list(nx.nodes(graph))

        self.textEdit_2.setText(str(s))

        st1 = "边数:" + str(size)
        st2 = "顶点数:" + str(ord)
        st3 = "特征值:" + str(np.real(value_))
        st4 = "顶点集:" + str(nodes)[1: -1]
        st5 = "PF向量:" + str(vector)[1: -1]

        if ord <= 6:
            s1 = st2 + '\n' + st1 + '\n' + st3 + '\n' + st4 + '\n' + st5
            self.textEdit.setText(s1)
        else:
            s1 = st2 + '\n' + st1 + '\n' + st3 + '\n'
            s1 += "顶点  分量\n"
            for i in range(ord):
                s1 += str(nodes[i])
                s1 += '  ' + str(vector[i])
                s1 += '\n'
            self.textEdit.setText(s1)

        s2 = str(s)[1: -1]
        self.textEdit_2.setText(' ' + s2)


if __name__ == '__main__':
    app = QApplication(sys.argv)

    main_window = MainWindow()
    main_window.show()

    sys.exit(app.exec_())
