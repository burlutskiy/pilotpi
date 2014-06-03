package test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import test.BerlinClockPrinter.Mode;

public class BerlinClock {
	BerlinClockPrinter printer = new SystemOutPrinter();
	
	public void print(String time) throws ParseException {
		Calendar calendar = parseAndGetCalendar(time);

		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		
		printTopLine(seconds);
		printFirstLine(hours);
		printSecondLine(hours);
		printThirdLine(minutes);
		printForthLine(minutes);
	}

	private Calendar parseAndGetCalendar(String time) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = dateFormat.parse(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	private void printForthLine(int minutes) {
		for (int i = 0; i < 4; i++) {
			printer.printForthLineLamp(minutes % 5 > i ? Mode.yellow : Mode.off);
		}
	}

	private void printThirdLine(int minutes) {
		for (int i = 0; i < 11; i++) {
			if((i+1) % 3 == 0){
				printer.printThirdLineLamp(minutes / (5 * (i + 1)) > 0 ? Mode.red : Mode.off);
			}
			else {
				printer.printThirdLineLamp(minutes / (5 * (i + 1)) > 0 ? Mode.yellow : Mode.off);
			}
		}
	}

	private void printSecondLine(int hours) {
		for (int i = 0; i < 4; i++) {
			printer.printSecondLineLamp(hours % 5 > i ? Mode.red : Mode.off);
		}
	}

	private void printFirstLine(int hours) {
		for (int i = 0; i < 4; i++) {
			printer.printFirstLineLamp(hours / (5 * (i+1)) > 0 ? Mode.red : Mode.off);
		}
	}

	private void printTopLine(int seconds) {
		printer.printTopLamp(seconds % 2 == 0 ? Mode.yellow : Mode.off);
	}
}
