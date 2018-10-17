package com.dd.data;

public class Brand {
	
	private String brand;
	private boolean selected;
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	@Override
	public String toString() {
		return "Brand [brand=" + brand + ", selected=" + selected + "]";
	}
	
	

}
