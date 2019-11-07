#!/usr/bin/python3
# -*- coding: UTF-8 -*-
# 文件名：__init__.py
import sys
import math
import cmath
import time
import os

# Let's start https://www.runoob.com/python/python-intro.html
print("hello world")
print("你好，世界")
sys.stdout.write("啊哈哈\n")

# 布尔判断取反
flag = False
if flag:
    print( "Ture")
elif not flag:
    print("false")
else:
    print("No")

# 字符串+基本操作
message = "nihaoya"
print(message)
print(message[1:4])
print(message[0:2])
print(message[:2])
print(message * 2)

# 链表
lista = ["dfdsf", "aaaa", 1999]
for item in lista:
    if isinstance(item, int):
        print("int类型: " + str(item))
        lista[2] = 2099
        print("int类型: " + str(lista[2]))
    if isinstance(item, str):
        print("str类型: " + item)

# 元组
tuples = ('aa', 11, True)
for item2 in tuples:
    # tuples[1] = 2099 wrong
    print(item2)

# 字典
tintyDic = {"name": "json", "code": 110}
print(tintyDic.keys())
print(tintyDic.values())

# 二维元组创建字典
dicStr = [('spam', 1), ('egg', 2), ('bar', 3)]
dicStr2Dic = dict(dicStr)
print(dicStr2Dic)

a = 10.0
b = 2.1
print(a / b)
print(a // b)          # 向下取整
print(math.floor(a/b)) # 向下取整
print(math.ceil(a/b))  # 向上取整

a = 10
b = 20

# 逻辑运算
if a and b:
    print ("1 - 变量 a 和 b 都为 true")
else:
    print ("1 - 变量 a 和 b 有一个不为 true")

if a or b:
    print ("2 - 变量 a 和 b 都为 true，或其中一个变量为 true")
else:
    print ("2 - 变量 a 和 b 都不为 true")

# while else
flag2 = True
while flag2:
    print ("test")
    flag2 = False
else:
    print ("玩球了")

for letter in "Hello, World!":
    print (letter,)

# math基本
print("\n")
print(dir(math))  # 基本数学运算
print(dir(cmath))  # 复数运算
print(math.sqrt(9))
# print math.sqrt(-1) wrong
print(cmath.sqrt(9))
print(cmath.sqrt(-1))  # 复数可以平方根

# 简单时间
print(time.time())
print(time.localtime(time.time()))
print(time.asctime(time.localtime()))

# 引入自定义模块py，可以重命名，可单独引入函数
# BASE_DIR=os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
# print("BASE_DIR=" + BASE_DIR)
# sys.path.append(BASE_DIR)
import funtion as test
print(test.other())
from funtion import go
print(go())

# Python3将raw_input和input进行整合成了input....去除了raw_input()函数....
# 其接受任意输入, 将所有输入默认为字符串处理,并返回字符串类型
if False:
    str = raw_input("请输入:")
    print(str)
    expre = input("可以输入表达式:")  # [x*5 for x in range(2,10,2)]
    print(expre)

# 异常简单捕获
try:
    os.mkdir("test\\test")
    os.makedirs("test\\test\\test\\test\\test")
    os.rename("ddd", "wwwww")
except Exception as err:
    print("IOError", format(err))
    pass
else:
    print("其他异常")
finally:
    print("最后走了呀")

# 类练习一把
class Test:
    gloabV = 0
    def output(self):
        print("我是一个类，我属于", self.__class__)
    def args_input(self, name, test, what, glab):
        self._name = name
        self.test2 = test
        self.what = what
        Test.gloabV = glab

    def output2(self):
        print(self._name, self.test2, self.what, Test.gloabV)

t = Test()
t.output()
t.args_input("嗯嗯", "test sb", True, 110)
t.output2()
del t
try:
    t.output()  # NameError: name 't' is not defined
except Exception as err:
    print(err)

'''
单下划线、双下划线、头尾双下划线说明：
__foo__: 定义的是特殊方法，一般是系统定义名字 ，类似 __init__() 之类的。

_foo: 以单下划线开头的表示的是 protected 类型的变量，即保护类型只能允许其本身与子类进行访问，不能用于 from module import *

__foo: 双下划线的表示的是私有类型(private)的变量, 只能是允许这个类本身进行访问了。
'''

# 正则需要单独学习下，像java正则这些都需要单独学习研究，有讲究的
# cgi可以玩下 https://www.runoob.com/python/python-cgi.html

# 数据库可以练习下MySql import MySQLdb

# 线程跑起来了
import _thread as thread
from threading import Thread
from threading import Lock

countervalue = 0
threadLock = Lock()

def thread_run(couternum=0)  :
    global countervalue
    while True:
        threadLock.acquire()
        countervalue += 1
        print("线程" + str(couternum) + "跑起来了: " + str(countervalue))
        threadLock.release()

thread.start_new_thread(thread_run, (11, ))
thread1 = Thread(target = thread_run, args = (12, ))
thread1.start()
thread2 = Thread(target = thread_run, args = (13, ))
thread2.start()

# 网络简单socket
import socket

s = socket.socket()
host = socket.gethostname()
port = 3333
print (host, port)
s.bind(("localhost", port))
s.listen(5)
while True:
    c, addr = s.accept()  # 一般address的格式为元组（hostname,port），如果连接出错，返回socket.error错误
    print (addr)  # 自己try catch ('\xe8\xbf\x9e\xe6\x8e\xa5\xe5\x9c\xb0\xe5\x9d\x80', ('192.168.207.2', 21264))
    print (addr[0])  # 来自哪里的ip地址
    print (addr[1])
    c.send("耍")
    c.close()

# 发送邮件smtplib 需要你本机已安装了支持 SMTP 的服务，如：sendmail

# 解析xml这些，针对练习即可，首先需要了解xml import xml.sax

# Json同理
# 使用第三方库：Demjson
# Demjson 是 python 的第三方模块库，可用于编码和解码 JSON 数据，包含了 JSONLint 的格式化及校验功能。
#
# Github 地址：https://github.com/dmeranda/demjson
#
# 官方地址：http://deron.meranda.us/python/demjson/


# 2.x 3.x区别可以了解下 https://www.runoob.com/python/python-2x-3x.html



