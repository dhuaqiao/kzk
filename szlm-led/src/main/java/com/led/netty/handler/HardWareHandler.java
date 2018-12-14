package com.led.netty.handler;

import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.pojo.LoginCommand;
import com.led.netty.pojo.SetUpTimeCommand;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HardWareHandler extends SimpleChannelInboundHandler<Object>{
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,Object msg) throws Exception { // (1)
		System.out.println("HardWareHandler channelRead0 ");
		System.out.println(msg);
		if(msg instanceof LoginCommand) {
			System.out.println("登录...");
			ctx.writeAndFlush(msg);
		}else if(msg instanceof HeartBeatCommand) {
			System.out.println("心跳...");
			ctx.writeAndFlush(msg);
		}else if(msg instanceof SetUpTimeCommand) {
			System.out.println("授时...");
			ctx.writeAndFlush(msg);
		}else{
			ctx.writeAndFlush(msg);
		}
	}
	

	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel incoming = ctx.channel();
        
        // Broadcast a message to multiple Channels
        //channels.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 加入"));
        
        //channels.add(incoming);
		System.out.println("Client:"+incoming.remoteAddress() +"加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
    	clear(ctx);
        Channel incoming = ctx.channel();
        
        // Broadcast a message to multiple Channels
        //channels.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 离开"));
        
		System.out.println("Client:"+incoming.remoteAddress() +"离开");

        // A closed Channel is automatically removed from ChannelGroup,
        // so there is no need to do "channels.remove(ctx.channel());"
    }
	    
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
		System.out.println("Client:"+incoming.remoteAddress()+"在线");
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
		clear(ctx);
        Channel incoming = ctx.channel();
		System.out.println("Client:"+incoming.remoteAddress()+"掉线");
		 ctx.channel().close();
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println(" channelUnregistered ");
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		System.out.println(" userEventTriggered ");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)	// (7)
			throws Exception {
		clear(ctx);
		System.out.println(" exceptionCaught ");
		
    	Channel incoming = ctx.channel();
		System.out.println("Client:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.channel().close();
	}
	
	public void clear(ChannelHandlerContext ctx){
	
	}

}
