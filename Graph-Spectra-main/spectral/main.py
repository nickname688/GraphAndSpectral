import sys
from PyQt5.QtWidgets import QApplication, QMainWindow, QMessageBox, QGraphicsPixmapItem, QGraphicsScene
from PyQt5.QtGui import QImage, QPixmap

import workM.ui.UIstart as start
from workM.SolverFixOrder import *
from workM.SolverFixSize import *
from workM.GraphGener import *
from workM.settingOper import *
from workM.MultCaluer import *

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
    # 第一个框按照列出发
    dataSetting = [0, 0, 1, 0, 1, 0, 2000, 0.2, 1]

    needRefresh = False

    def __init__(self, parent=None):
        super(MainWindow, self).__init__(parent)
        self.thread = None
        self.setupUi(self)

        self.addEdgeBtn.clicked.connect(partial(self.AddEdge, self, self))
        self.delEdgeBtn.clicked.connect(partial(self.DelEdge, self, self))
        self.delVerBtn.clicked.connect(partial(self.DelVertex, self, self))

        self.freshforbidGraphBtn.clicked.connect(partial(self.freshForbidGraph, self))
        self.freshAnsGraphBtn.clicked.connect(partial(self.FreshAnsGraph, self.ansGr))

        self.StartCalBtn.clicked.connect(partial(self.startCal, self, self))
        self.forBidGraphSelectComboBox.currentIndexChanged.connect(partial(self.SelectChanged))

        self.moreSetting.clicked.connect(partial(self.ClickSetting, self))

        self.needRefresh = False

    # # 鼠标放下事件
    # def mouseReleaseEvent(self, event):
    #     print()
    #     if self.needRefresh:
    #         self.freshForbidGraph(event)
    #         self.FreshAnsGraph(self.gr)
    #         self.needRefresh = False
    #
    # # 窗口大小变化事件
    # def resizeEvent(self, event):
    #     print()
    #     self.needRefresh = True

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

    # 开始计算
    def startCal(self, e, mw):
        n = e.mnParaLine.text()
        if n == "":
            QMessageBox.critical(mw, "错误", "未输入参数")
            return 0

        num = None

        try:
            num = int(n)
        except:
            QMessageBox.critical(mw, "错误", "参数无效")
            return 0

        m = num
        time = self.dataSetting[-3]
        k1 = self.dataSetting[-2]

        ifPlanar = self.dataSetting[0]
        ifOutPlanar = self.dataSetting[1]
        ifMultCore = self.dataSetting[-1]
        ifTuran = self.dataSetting[3]

        if self.gr is None or self.gr.order() == 0 or self.gr.number_of_edges() == 0:
            self.gr = None

        self.StartCalBtn.setEnabled(False)

        # 如果需要多核计算，开拓一个新的对象进行处理
        if ifMultCore > 1:
            # 运行次数 边数/点数 概率系数 平面性 外平面性 禁用图 多核运行个数
            para = [time, m, k1, ifPlanar, ifOutPlanar, self.gr, ifMultCore]
            # 固定边/顶点 图兰数/谱问题
            para1 = [self.dataSetting[4] == 1, ifTuran] #1固定边
            calcu = MultCaluer(self, para, para1)

            calcu.run()
            return

        if self.dataSetting[4] == 1:
            self.thread = SolverFixSize(times=time, order=m, k1=k1, ifPlanar=ifPlanar, ifOutPlanar=ifOutPlanar,
                                        banGraph=self.gr, ifMultCore = ifMultCore)

            self.thread.finishSignal.connect(self.ChangeRoll)
            self.thread.graphSignal.connect(self.SetAnsGraph)

            self.thread.start()

        elif self.dataSetting[5] == 1:
            self.thread = SolverFixOrder(times=time, order=m, k1=k1, ifPlanar=ifPlanar, ifOutPlanar=ifOutPlanar,
                                         banGraph=self.gr, ifTuran=ifTuran, ifMultCore = ifMultCore)

            self.thread.finishSignal.connect(self.ChangeRoll)
            self.thread.graphSignal.connect(self.SetAnsGraph)

            self.thread.start()

        # threadCount = LoadingRoll(t=time)
        #
        # ansSp, ansGraph = GetExternalGraphMain(m, gr, time, k1, e)

    # 选中的禁用子图发生改变
    def SelectChanged(self):
        graphText = self.forBidGraphSelectComboBox.currentText()

        if graphText == "自定义" or graphText == "选择禁用子图":
            return

        graphGener = GraphGener()

        self.gr = graphGener.GetGraph(graphText)
        self.freshForbidGraph(self)

    def ChangeRoll(self, msg):
        if msg[-1] == '%':
            self.textEdit.setText(msg)
        else:
            te = str(msg + '\n')
            matrix = np.matrix(nx.adjacency_matrix(self.ansGr).todense())
            matStr = str(matrix)

            self.textEdit.setText(te + matStr)

    def ClickSetting(self, e):
        self.settingWindow.ModifyData(self.dataSetting)
        self.settingWindow.show()

    # 更新界面显示
    def UpdateInterface(self):
        if self.dataSetting[4] == 1:
            self.label_4.setText("边数")
        else:
            self.label_4.setText("顶点数")


if __name__ == '__main__':
    app = QApplication(sys.argv)

    main_window = MainWindow()
    main_window.show()

    setWindow = settingOper(parent=main_window)
    main_window.settingWindow = setWindow

    # MainWindow = QMainWindow()
    # ui = start.Ui_MainWindow()
    # ui.setupUi(MainWindow)
    # MainWindow.show()

    sys.exit(app.exec_())
