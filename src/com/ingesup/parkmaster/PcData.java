package com.ingesup.parkmaster;

import java.util.ArrayList;
import java.util.Map;

public class PcData  implements java.io.Serializable{
	private float cpu;
	private float ram;
	private Map<String, Float> disk;
	public PcData() {
		super();
	}
	public PcData(float cpu, float ram) {
		super();
		this.cpu = cpu;
		this.ram = ram;
	}
	public PcData(float cpu, float ram, Map<String, Float> disk) {
		super();
		this.cpu = cpu;
		this.ram = ram;
		this.disk = disk;
	}
	public float getCpu() {
		return cpu;
	}
	public void setCpu(float cpu) {
		this.cpu = cpu;
	}
	public float getRam() {
		return ram;
	}
	public void setRam(float ram) {
		this.ram = ram;
	}
	public Map<String, Float> getDisk() {
		return disk;
	}
	public String getDiskString() {
		String disks="";
		for (String key: disk.keySet()) {
			disks+=key+"_"+disk.get(key)+" ";
		}
		return disks;
	}
	public void setDisk(Map<String, Float> disk) {
		this.disk = disk;
	}
}
