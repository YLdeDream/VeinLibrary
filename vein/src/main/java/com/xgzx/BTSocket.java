package com.xgzx;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;


import com.xgzx.veinmanager.VeinApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;




public class BTSocket {
	private static final String TAG = "BluetoothChatService";
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothDevice mmDevice;
	private static BluetoothSocket mmSocket = null;
	public static InputStream mmInStream;
	public static OutputStream mmOutStream;

	public BTSocket() {
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("MissingPermission")
	public boolean connect(BluetoothDevice device) {
		mmDevice = device;
		try {
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		}
		try {
			mmSocket.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			changeBTConnectMode(mmDevice, e);
			return false;
		}
		try {
			mmInStream = mmSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mmOutStream = mmSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VeinApi.PrintfDebug("蓝牙连接成功");
		return true;
	}

	public int Read(byte[] buffer, int byteOffset, int byteCount, int timeout) {
		int bytes = byteOffset;
		int timecnt = 0;
		while(true)
		{
			int ret = 0;
			try {
				ret = mmInStream.available();
			} catch (IOException e) {
			}
			if(ret >= byteCount) {
				try {
					ret = mmInStream.read(buffer, bytes, byteCount - bytes);
					if (ret < 0) return -1;
					bytes += ret;
					if (bytes >= byteCount) return bytes;
				} catch (IOException e) {
					return -2;
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
			if(timecnt++ > timeout) return -1;
		}
	}

	public int Write(byte[] buffer, int size) {
		VeinApi.PrintfDebug("BTW:" + size);
		try {
			mmOutStream.write(buffer, 0, size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return -1;
		}
		return size;
	}

	@SuppressLint("MissingPermission")
	private boolean changeBTConnectMode(BluetoothDevice mmDevice, Exception e) {
		Method m = null;
		boolean connectOk = false;
		if (e.toString().equals("java.io.IOException: Connection refused")) {
			try {
				mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
				connectOk = true;
			} catch (IOException e2) {
				VeinApi.PrintfDebug("更换socket创建方式" + e2.toString());
			}
		} else if (e.toString().equals("java.io.IOException: Unable to start Service Discovery")) {
			try {
				if (m == null) {
					m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				}
				mmSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
				connectOk = true;
				VeinApi.PrintfDebug("Unable to start Service Discovery,更换socket创建方式成功");
			} catch (Exception e3) {
				VeinApi.PrintfDebug("更换socket创建方式" + e3.toString());
			}
		}
		return connectOk;
	}

	public static void CloseSocket() {
		try {
			mmInStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mmOutStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mmSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
