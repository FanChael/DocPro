#!/usr/bin/python3
# -*- coding: UTF-8 -*-
# 文件名：crawler.py

from lxml import etree
from urllib import request
import urllib.parse
from fake_useragent import UserAgent
import time
import selenium.webdriver.support.ui as ui
from selenium.webdriver.chrome.options import Options
from selenium import webdriver

ua = UserAgent()
headers = {
    'User-Agent': "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Mobile Safari/537.36",
    'Host': 'i.snssdk.com',
    'Referer': 'https://i-hl.snssdk.com/feoffline/hot_list/template/hot_list/',
    'Sec-Fetch-Mode': 'cors',
    'Sec-Fetch-Site': 'same-site'
}

# 再来个获取公开政策的网站 - 需要伪装成浏览器访问；不知道这样举例恰当不，政策网站还是不要乱爬吧！算了，改成大学的吧..我母校...
if False:
    params = {'ev_type': 'custom',
              'cm_name': 'hot_list_ajax_num',
              'cm_type': 'count',
              'cm_tag': 'new_all',
              'version': '2.1.4',
              'hostname': 'i-hl.snssdk.com',
              'protocol': 'https',
              'url': 'https://i-hl.snssdk.com/feoffline/hot_list/template/hot_list/',
              'slardar_session_id': '33dde665-bd71-41f2-92fe-479ebfe07733',
              'sample_rate': 1,
              'pid': 'hot_list',
              'report_domain': 'i.snssdk.com',
              'screen_resolution': '400x63',
              'network_type': '4g',
              'bid': 'toutiao_ugc_wap',
              'context': {},
              'slardar_web_id': '380d36f6-fd37-42e8-807a-0c31b6419af2',
              'timestamp': time.time()}  # 时间戳新的话，获取的内容可能比较新？？
    params = urllib.parse.urlencode(params)
    url = 'https://i-hl.snssdk.com/feoffline/hot_list/template/hot_list?%s' % params
    print(url)
    req = request.Request(url, None, headers)
    data = ''
    with request.urlopen(req) as uf:
        while True:
            data_temp = uf.read(1024)
            if not data_temp:
                break
            data += data_temp.decode('utf-8', 'ignore')

    # 2. 解析获取需要内容
    if data:
        print(data)  # 这内容目前还不是真正的内容，卧槽....
        # # 转换为html对象，以便进行path查找
        # html_object = etree.HTML(data)
        # # 补全网页吧
        # last_html_data = etree.tostring(html_object)
        # # 再次转换为html对象，以便进行path查找
        # html_object = etree.HTML(last_html_data)
        # # 从网页工具xPath里面去获取规则，根据路径一层层往下找就行（这样能精准一些，但是如果标识都一样，且很多，数据就会很多
        # path_data = html_object.xpath('//*[@id="talented"]/div[2]/ul/li/span/a/@href')  # //*[@id="talented"]/div[2]/ul/li/span/a/@href
        # # 专注人才培养[@id="talented"]下面的列表
        # for item in path_data:
        #     print(item)  # 得到路径补全自己去特殊补全

# 启动参数
chrome_options = Options()
chrome_options.add_argument('--headless')
# 禁止加载图片
chrome_options.add_argument('--disable-gpu')
# 禁止打印日志
chrome_options.add_argument('log-level=3')
# 静默启动
browser = webdriver.Chrome(chrome_options=chrome_options)
# 浏览器静默加载页面后等待10s(这个页面加载后后续还有很多js要执行,所以我们需要时间获取最终页面)
wait = ui.WebDriverWait(browser, 10)
# 获取页面+包含诸多js
browser.get('https://i-hl.snssdk.com/feoffline/hot_list/template/hot_list')
# 当前页面可以下载下来，里面调用了一些js来加载数据鸭
# html = browser.page_source
# if html:
#     print(html.encode("utf8"))
#     with open('./1.html', mode='w', encoding='utf-8') as f:
#         f.write(html)
# 如果网页里面能find_elements_by_xpath找个元素的话,直接返回无需再等待
wait.until(lambda driver: driver.find_elements_by_xpath('//div[@class="card-title"]'))
# 之后开始获取标题内容 - 记得利用xpath工具查看验证标签，可行才能用下面方法获取
title = browser.find_elements_by_xpath('//div[@class="card-title"]')
desc = browser.find_elements_by_xpath('//div[@class="left-desc"]')
for idx, val in enumerate(title):
    print(idx)
    print('标题:', val.text)
    print('内容:', desc[idx].text)
# 退出浏览器进程
browser.quit()

# 一些个尝试看哈....
# 执行页面js脚本 - 这里这样执行不行滴，别闹了....
# jsStr = browser.execute_script('//s3.pstatp.com/toutiao/feoffline/hot_list/resource/hot_list/js/vendor.28884c21.chunk.js')
# print(jsStr)

# 执行一些浏览器开发者工具XHR获取的请求链接
# browser.get('https://i.snssdk.com/log/sentry/v2/api/slardar/main/?ev_type=pageview&version=2.1.4&hostname=i-hl.snssdk.com&protocol=https&url=https%3A%2F%2Fi-hl.snssdk.com%2Ffeoffline%2Fhot_list%2Ftemplate%2Fhot_list%2F&slardar_session_id=0dd94f29-dd76-45bd-bf9a-d78b45545514&sample_rate=1&pid=hot_list&report_domain=i.snssdk.com&screen_resolution=400x145&network_type=4g&bid=toutiao_ugc_wap&context=%7B%7D&slardar_web_id=380d36f6-fd37-42e8-807a-0c31b6419af2&timestamp=1573456312282')
# time.sleep(10)
# # 当前页面可以下载下来，调用了js
# html = browser.page_source
# if html:
#     print(html.encode("utf8"))

