package com.ingesup.parkmaster;

import java.beans.MethodDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.DocFlavor.STRING;

import java.util.Map.Entry;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.CpuTimer;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.json.JSONException;
import org.json.JSONObject;

public class Agent {

	public static void main(String[] args) {
		PcData pcData;
		float totalRame = 0;
		float freeRame = 0;
		Sigar sigar = new Sigar();
		while (true) {
			pcData = new PcData();
			OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
			for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
				method.setAccessible(true);
				if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
					Object value;
					try {
						value = method.invoke(operatingSystemMXBean);
					} catch (Exception e) {
						value = e;
					}
					if (method.getName().equals("getTotalPhysicalMemorySize"))
						totalRame = Float.parseFloat(value.toString()) / 1024 / 1024 / 1024;
					else if (method.getName().equals("getFreePhysicalMemorySize"))
						freeRame = Float.parseFloat(value.toString()) / 1024 / 1024 / 1024;
				}
			}
			pcData.setRam((100 - ((freeRame / totalRame) * 100)));

			/* Get a list of all filesystem roots on this system */
			File[] roots = File.listRoots();

			/* For each filesystem root, print some info */
			Map<String, Float> disk = new HashMap<>();
			for (File root : roots) {
				try {
					disk.put(root.getAbsolutePath(),
							(100 - ((float) root.getFreeSpace() / (float) root.getTotalSpace()) * 100));
				} catch (Exception e) {
					System.out.println("0");
				}
			}
			pcData.setDisk(disk);

			CpuPerc perc = null;
			try {
				perc = sigar.getCpuPerc();
			} catch (SigarException e2) {
				e2.printStackTrace();
			}
			pcData.setCpu((float) (perc.getCombined() * 100));
			System.out.println(pcData.getRam() + " " + pcData.getCpu() + " " + pcData.getDisk());
			send(pcData);
			System.out.println("");
			System.out.println("");
			System.out.println("");
			try {
				Thread.sleep(1000*30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void send(PcData pcData) {
		JSONObject json = new JSONObject();
	    try{
	        json.put("Ram",pcData.getRam());
	        json.put("Cpu",pcData.getCpu());
	        json.put("Disk",pcData.getDiskString());
	    } catch(JSONException jsone){

	    }
	    
	    URL url;
	    HttpURLConnection connection = null;
	    ObjectOutputStream out;
	    try {
	        url = new URL("http://localhost:8080/WS-MASTERE-IS/update");     //Creating the URL.
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setUseCaches(false);
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        //Send request
	        OutputStream os = connection.getOutputStream();
	        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
	        System.out.println(json.toString());
	        osw.write(json.toString());
	        osw.flush();
	        osw.close();
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            System.out.println("Ok response");
	        } else {
	            System.out.println("Bad response");
	        }
	    } catch (Exception ex) {
	       // ex.printStackTrace();
	    	System.out.println(ex);
	    }
	}

	public static void send2(PcData pcData) {
		try {
			String url = "http://localhost:8080/WS-MASTERE-IS/update";
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");
			
			JSONObject parent = new JSONObject();
			parent.putOnce("Data", pcData.getRam());

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(parent.toString());
			wr.flush();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
}
