from functools import partial

from workM.ui.UIsettings import *
from PyQt5.QtWidgets import QApplication, QMainWindow, QMessageBox


# 高级设置窗口
class settingOper(QMainWindow, Ui_setting):
    parent = None

    def __init__(self, parent=None):
        super(settingOper, self).__init__(parent)
        self.setupUi(self)
        self.parent = parent

        self.yesBtn.clicked.connect((partial(self.ClickYes)))
        self.cancelBtn.clicked.connect((partial(self.ClickCancel)))

        self.planarBtn.clicked.connect((partial(self.ClickplanarBtn)))
        self.outPlanarBtn.clicked.connect((partial(self.ClickoutPlanarBtn)))
        self.spectrumBtn.clicked.connect((partial(self.ClickspectrumBtn)))
        self.turanBtn.clicked.connect((partial(self.ClickturanBtn)))
        self.fixEdgeBtn.clicked.connect((partial(self.ClickfixEdgeBtn)))
        self.fixVertexBtn.clicked.connect((partial(self.ClickfixVertexBtn)))
        self.multCoreBtn.clicked.connect((partial(self.ClickmultCoreBtn)))

    def ClickmultCoreBtn(self):
        if self.multCoreBtn.isChecked():
            self.multCoreNumber.setEnabled(True)
            self.multCoreNumber.setText('1')
        else:
            self.multCoreNumber.setEnabled(False)
            self.multCoreNumber.setText('')

    def ClickplanarBtn(self):
        if not self.planarBtn.isChecked():
            self.outPlanarBtn.setChecked(False)

    def ClickoutPlanarBtn(self):
        if self.outPlanarBtn.isChecked():
            self.planarBtn.setChecked(True)

    def ClickspectrumBtn(self):
        if self.turanBtn.isChecked():
            self.turanBtn.setChecked(False)
            self.fixEdgeBtn.setChecked(True)
            self.fixEdgeBtn.setCheckable(True)
            self.fixEdgeBtn.setEnabled(True)
            self.fixVertexBtn.setChecked(False)
            self.runTimeEditLine.setText(str(2000))
            self.rateFactLineEdit.setText(str(0.2))

        else:
            self.spectrumBtn.setChecked(True)

    def ClickturanBtn(self):
        if self.spectrumBtn.isChecked():
            self.spectrumBtn.setChecked(False)
            self.fixEdgeBtn.setChecked(False)
            self.fixEdgeBtn.setFocus(False)
            self.fixEdgeBtn.setEnabled(False)
            self.fixVertexBtn.setChecked(True)
            self.runTimeEditLine.setText(str(300))
            self.rateFactLineEdit.setText(str(0.2))

        else:
            self.turanBtn.setChecked(True)

    def ClickfixEdgeBtn(self):
        if not self.fixEdgeBtn.isCheckable():
            return

        if self.fixVertexBtn.isChecked():
            self.runTimeEditLine.setText(str(2000))
            self.rateFactLineEdit.setText(str(0.2))
            self.fixVertexBtn.setChecked(False)
        else:
            self.fixEdgeBtn.setChecked(True)

    def ClickfixVertexBtn(self):
        if not self.fixEdgeBtn.isCheckable():
            return

        if self.fixEdgeBtn.isChecked():
            self.runTimeEditLine.setText(str(300))
            self.rateFactLineEdit.setText(str(0.2))
            self.fixEdgeBtn.setChecked(False)
        else:
            self.fixVertexBtn.setChecked(True)

    # 点击确定
    def ClickYes(self):
        flag = self.UpLoadDataParent()
        if flag == 0:
            return

        self.parent.UpdateInterface()
        self.close()

    # 点击取消
    def ClickCancel(self):
        self.close()
        pass

    # 上传数据给父窗口
    def UpLoadDataParent(self):
        data = []

        da1 = 1 if self.planarBtn.isChecked() else 0
        da2 = 1 if self.outPlanarBtn.isChecked() else 0
        da3 = 1 if self.spectrumBtn.isChecked() else 0
        da4 = 1 if self.turanBtn.isChecked() else 0
        da5 = 1 if self.fixEdgeBtn.isChecked() else 0
        da6 = 1 if self.fixVertexBtn.isChecked() else 0

        da7 = self.runTimeEditLine.text()
        try:
            da7 = int(da7)
            if da7 <= 0:
                raise Exception("")
        except:
            QMessageBox.critical(self, "错误", "参数\'运行次数\'有误")
            return 0

        da8 = self.rateFactLineEdit.text()
        try:
            da8 = float(da8)
            if da8 > 1 or da8 <= 0:
                raise Exception("")
        except:
            QMessageBox.critical(self, "错误", "参数\'概率因子\'有误")
            return 0

        da9 = None
        if not self.multCoreBtn.isChecked():
            da9 = 1
        else:
            da9 = self.multCoreNumber.text()
            try:
                da9 = int(da9)
                if da9 > 9 or da9 <= 0:
                    raise Exception("")
            except:
                QMessageBox.critical(self, "错误", "参数\'运行数量\'有误")
                return 0


        data = [da1, da2, da3, da4, da5, da6, da7, da8, da9]

        self.parent.dataSetting = data
        return 1

    # 显示窗口初始化函数
    def ModifyData(self, data):
        if data[0] == 1:
            self.planarBtn.setChecked(True)
        if data[1] == 1:
            self.outPlanarBtn.setChecked(True)

        if data[2] == 1:
            self.spectrumBtn.setChecked(True)
        if data[3] == 1:
            self.turanBtn.setChecked(True)
            self.fixEdgeBtn.setCheckable(False)

        if data[4] == 1:
            self.fixEdgeBtn.setChecked(True)
        if data[5] == 1:
            self.fixVertexBtn.setChecked(True)

        self.runTimeEditLine.setText(str(data[6]))
        self.rateFactLineEdit.setText(str(data[7]))

        if data[8] == 1:
            self.multCoreBtn.setChecked(False)
            self.multCoreNumber.setEnabled(False)
            self.multCoreNumber.setText('')
        else:
            self.multCoreBtn.setChecked(True)
            self.multCoreNumber.setText(str(data[8]))
