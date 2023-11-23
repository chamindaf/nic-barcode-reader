package com.lowcode.combank.barcode;

class Address {
	String city;
	String addrrssLine1;
	String addressLine2;

}

public class Utils {

	public static Address getAddress(String barcodeAddress) {

		Address address = new Address();
		if(barcodeAddress == null || barcodeAddress.isEmpty())
			return null;

		String s = removeLastChar(barcodeAddress);
		String[] arr = s.split(",");
		address.city = arr[arr.length - 1];
		address.addrrssLine1 = arr[0];
		int sPos = s.indexOf(",");
		int lPos = s.lastIndexOf(",");
		address.addressLine2 = s.substring(sPos + 1, lPos);
		return address;
	}

	public static String removeLastChar(String s) {
		return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
	}

}
