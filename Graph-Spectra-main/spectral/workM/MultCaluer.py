from functools import partial

from workM.SolverFixOrder import *
from workM.SolverFixSize import *
import multiprocessing
from multiprocessing.managers import SharedMemoryManager
from multiprocessing import Process


# 多进程计算 工具
class MultCaluer(QThread):
    orderNum = pyqtSignal(int)
    finishSignal = pyqtSignal(str)
    graphSignal = pyqtSignal(type(nx.Graph()))

    parent = None
    processNum = 1
    para = None
    ifSize = False
    ifTuran = False

    # # 运行次数 边数/点数 概率系数 平面性 外平面性 禁用图 多核运行个数
    # para = [time, m, k1, ifPlanar, ifOutPlanar, self.gr, ifMultCore]
    # # 固定边/顶点 图兰数/谱问题
    # para1 = [self.dataSetting[4] == 1, ifTuran]  # 1固定边

    time = None
    m = None
    k1 = None
    ifPlanar = None
    ifPlanar = None
    ifOutPlanar = None
    gr = None
    coreNumber = None

    thread = []

    def __init__(self, parent, para, para1):

        self.parent = parent
        t = multiprocessing.cpu_count() / 2
        t = math.floor(t)
        if t < 1:
            t = 1
        self.processNum = t

        self.para = para
        self.ifSize = para1[0]
        self.ifTuran = para1[1]

        self.time = para[0]
        self.m = para[1]
        self.k1 = para[2]
        self.ifPlanar = para[3]
        self.ifOutPlanar = para[4]
        self.gr = para[5]
        self.coreNumber = para[6]

    # 这里开始用多进程的方法
    def run(self):
        if self.ifSize:
            for i in range(self.coreNumber):
                pass
                # thr = SolverFixSize(times=self.time, order=self.m, k1=self.k1, ifPlanar=self.ifPlanar,
                #                             ifOutPlanar=self.ifOutPlanar,
                #                             banGraph=self.gr, ifMultCore=self.coreNumber)
                #
                # self.thread.append(thr)
