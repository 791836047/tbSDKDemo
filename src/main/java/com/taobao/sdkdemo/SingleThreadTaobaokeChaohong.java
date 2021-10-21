package com.taobao.sdkdemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.internal.util.StringUtils;
import com.taobao.api.request.TbkRtaConsumerMatchRequest;
import com.taobao.api.response.TbkRtaConsumerMatchResponse;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 单线程脚本
 */
public class SingleThreadTaobaokeChaohong {
        public final static String sources = "D:\\download\\resources\\chaohong01.txt";
        public final static String destnation="D:\\download\\resources\\a.txt";

        public static void main(String[] args) {//https请求地址
                File f = new File(destnation);
                try {
                        BufferedWriter output = new BufferedWriter(new FileWriter(f, false));//true,则追加写入text文本
                        output.write("");
                        output.flush();
                        output.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                SingleThreadTaobaokeChaohong.readText();
        }

        public static String readText() {
                List<String> inputList = null;
                try {
                        BufferedReader br = new BufferedReader(new FileReader(sources));//建立一个对象，它把文件内容转成计算机能读懂的语言
                        String line;
                        while ((line = br.readLine()) != null) {
//一次读入一行数据
                                String[] inputdatas = line.split(",");
//StringrelationId=null;
//StringspecialId=null;
                                String phone_number = null;
                                int i = 0;
                                for (String s : inputdatas) {
                                        System.out.print("----" + s + "---");
//if(i==3)relationId=s;
//if(i==4)specialId=s;
                                        if (i == 2) {
                                                phone_number = s;
                                        }
                                        i++;
                                }
                                String is_new_user = JudgeChaoHongIsNewUser(phone_number);
                                System.out.println();
                                if ((is_new_user != null) && is_new_user.equals("0")) {
                                        inputList = new ArrayList<String>();
                                        for (String d : inputdatas) {
                                                inputList.add(d);
                                        }
                                        inputList.add(is_new_user);
                                        String str = StringUtils.join(inputList, ",");
                                        SingleThreadTaobaokeChaohong.writeToFile(str);
                                        System.out.println(inputList);
                                }
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return null;
        }

        public static void writeToFile(String str) {
                File f = new File(destnation);
                if (f.exists()) {
                        System.out.print("文件存在");
                } else {
                        System.out.print("文件不存在");
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
                TaobaoClient client = new DefaultTaobaoClient("https://eco.taobao.com/router/rest", "25545902", "8263fce6c29a4fb4925e5a9daa228654");
                TbkRtaConsumerMatchRequest req = new TbkRtaConsumerMatchRequest ();
//apitaobao.tbk.dg.vegas.send.status(淘宝客-推广者-超级红包领取状态查询)
//req.setRelationId(relationId);
//req.setSpecialId(specialId);
                String md5DigestAsHex = DigestUtils.md5DigestAsHex(phone_number.getBytes());
                req.setDeviceValue(md5DigestAsHex);
                req.setDeviceType("MOBILE");
                TbkRtaConsumerMatchResponse rsp = null;
                String is_new_user = null;
                try {
                        rsp = client.execute(req);
                        if (rsp.isSuccess()) {
                                String s = rsp.getBody();
                                System.out.println(s);
                                JSONObject jsonObject = JSON.parseObject(s);
                                JSONObject name = jsonObject.getJSONObject("tbk_dg_vegas_send_status_response");
                                if (name != null && !name.equals(""))//空值校验异常校验
                                {
                                        JSONObject jsonObject1
                                                = jsonObject.getJSONObject("tbk_dg_vegas_send_status_response").getJSONObject("data").getJSONObject("result_list");
                                        List<Object> map_data = jsonObject1.getJSONArray("map_data");
                                        JSONArray jsonArray = new JSONArray(map_data);
                                        for (int i = 0; i < map_data.size(); i++) {
                                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                                is_new_user = String.valueOf(jsonObject2.getInteger("is_new_user"));
                                        }
                                } else {
                                        return "1";
                                }
                        } else {
                                System.out.println("响应失败");
                                System.out.println(rsp.getBody());
                                return "1";
//响应报文和请求报文里面的API个数一致，响应顺序也会和请求顺序保持一致。比如你提交15条请求，可能会出现10条成功，3条流控，2条ＨＳＦ服务提供端异常；但是我们的响应体依然会有１５条请求响应。
                        }
                } catch (ApiException e) {
                        e.printStackTrace();
                }
                System.out.println(rsp.toString());
//System.out.println(rsp.toString());
                return is_new_user;
        }
}
