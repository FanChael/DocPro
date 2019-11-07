#!/usr/bin/python3
# -*- coding: UTF-8 -*-
# 文件名：client.py

import socket

skt = socket.socket()
host = socket.gethostname()
port = 3333
skt.connect(("127.0.0.1", port))
print(skt.recv(1024))
skt.close()