package com.taobao.sdkdemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.AutoRetryTaobaoClient;
import com.taobao.api.internal.util.StringUtils;
import com.taobao.api.request.TbkRtaConsumerMatchRequest;
import com.taobao.api.response.TbkRtaConsumerMatchResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 线程池脚本
 * @author liaowenhui
 * @date 2021/10/20 17:19
 */
public class FixedThreadPoolChaohong_1111 {
    public final static String sourcesFile = "D:\\download\\resources\\source.txt";
    public final static String destnationFile = "D:\\download\\resources\\destnation.txt";

    public static void main(String[] args) {
        File f = new File(destnationFile);
        try {
            //true,则追加写入text文本
            BufferedWriter output = new BufferedWriter(new FileWriter(f, false));
            //每次执行前清空别写入文件的内容
            output.write("");
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readMyText();
    }

    //用线程池读取文件
    public static String readMyText() {
        int THREAD_NUMS = 10;
        try {
            //建立一个对象，它把文件内容转成计算机能读懂的语言
            BufferedReader br = new BufferedReader(new FileReader(sourcesFile));
            ExecutorService service = Executors.newFixedThreadPool(THREAD_NUMS);
            for (int i = 0; i < THREAD_NUMS; i++) {
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        String line;
                        List<String> inputList = null;
                        while (true) {
                            try {
                                if (!((line = br.readLine()) != null)) {
                                    break;
                                }
                                //一次读入一行数据
                                String[] inputdatas = line.split(",");
                                String phone_number = null;
                                int i = 0;
                                for (String s : inputdatas) {
                                    //System.out.print("----" + s + "---");
                                    if (i == 2) {
                                        phone_number = s;
                                    }
                                    i++;
                                }
                                //调用淘宝客-推广者-定向活动目标发布
                                String is_new_user = JudgeChaoHongIsNewUser(phone_number);
                                System.out.println();
                                if ((is_new_user != null)) {
                                    //每行记录创建一个数组列表对象
                                    inputList = new ArrayList<String>();
                                    for (String d : inputdatas) {
                                        inputList.add(d);
                                    }
                                    inputList.add(is_new_user);
                                    String str = StringUtils.join(inputList, ",");
                                    writeToMyFile(str);
                                    //System.out.println(inputList);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            service.shutdown();//线程结束后要关闭线程池，否则会一直挂着占用资源
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeToMyFile(String str) {
        File f = new File(destnationFile);
        if (f.exists()) {
            //System.out.print("文件存在");
        } else {
            //System.out.print("文件不存在");
            try {
                f.createNewFile();//不存在则创建
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(f, true));//true,则追加写入text文本
            output.write(str);
            output.write("\r\n");//换行
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String JudgeChaoHongIsNewUser(String phone_number) {
//apitaobao.tbk.dg.vegas.send.status(淘宝客-推广者-超级红包领取状态查询)
        String serverUrl = "https://eco.taobao.com/router/rest";
        String appKey = "25545902";
        String appSecret = "8263fce6c29a4fb4925e5a9daa228654";
        AutoRetryTaobaoClient client = new AutoRetryTaobaoClient(serverUrl, appKey, appSecret);
        client.setMaxRetryCount(2);
        TbkRtaConsumerMatchRequest  request = new TbkRtaConsumerMatchRequest();
        request.setAdzoneId(101201400353L);
        List<TbkRtaConsumerMatchRequest.OfferList> list = new ArrayList<>();
        TbkRtaConsumerMatchRequest.OfferList obj= new TbkRtaConsumerMatchRequest.OfferList();
        list.add(obj);
        obj.setOfferId("18567");
        request.setOfferList(list);
        request.setSpecialId(phone_number);

        TbkRtaConsumerMatchResponse response = null;
        String status = null;
        try {
            response = client.execute(request);
            String responseBody = response.getBody();
            //用alibaba.fastjson将json字符串转换成json对象
            JSONObject jsonObject = JSON.parseObject(responseBody);
            JSONObject name = jsonObject.getJSONObject("tbk_rta_consumer_match_response");
            //空值校验或异常校验
            if (name != null && !"".equals(name))
            {
                JSONObject jsonObject1
                        = name.getJSONObject("data").getJSONObject("result_list");
                List<Object> resultlist = jsonObject1.getJSONArray("resultlist");//获取json数组放到数组列表或者其他容器中，主要为了取索引位置
                JSONArray jsonArray = new JSONArray(resultlist);
                for (int i = 0; i < resultlist.size(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    status = String.valueOf(jsonObject2.getInteger("status"));//获取的也是1
                }
                //System.out.println(resultlist.get(0));
            } else {
                return "-1";
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
//System.out.println(response.getBody());
//System.out.println(rsp.toString());
        return status;
    }
}



