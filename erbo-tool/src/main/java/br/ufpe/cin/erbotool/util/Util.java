package br.ufpe.cin.erbotool.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Util {

	public static String getFormatedDateYYYYMMDDHHmmss(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String yyyyMMdd = sdf.format(date);
		return yyyyMMdd;
	}
}
