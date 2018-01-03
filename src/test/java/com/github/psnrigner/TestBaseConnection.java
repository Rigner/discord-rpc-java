package com.github.psnrigner;

import com.github.psnrigner.discordrpcjava.impl.BaseConnection;
import com.github.psnrigner.discordrpcjava.impl.RpcConnection;

public class TestBaseConnection {


     // needs to be tested for Unix and Mac OSX
    public static void main(String[] args) {
        RpcConnection rpc = RpcConnection.create("test");
        BaseConnection connection = rpc.getBaseConnection();
        System.out.println(connection);
    }
}
