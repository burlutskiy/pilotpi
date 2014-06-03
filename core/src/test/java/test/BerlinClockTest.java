package test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.ParseException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import test.BerlinClockPrinter.Mode;

@RunWith(MockitoJUnitRunner.class)
public class BerlinClockTest {

	@InjectMocks 
	BerlinClock berlinClock = new BerlinClock();
	
	@Spy
	BerlinClockPrinter berlinClockPrinter = new SystemOutPrinter();
	
	@Test
	public void print_235959() throws ParseException{
		berlinClock.print("23:59:59");
		verify(berlinClockPrinter, times(1)).printTopLamp(eq(Mode.off));
		
		verify(berlinClockPrinter, times(4)).printFirstLineLamp(eq(Mode.red));
		
		verify(berlinClockPrinter, times(3)).printSecondLineLamp(eq(Mode.red));
		verify(berlinClockPrinter, times(1)).printSecondLineLamp(eq(Mode.off));
		
		verify(berlinClockPrinter, times(8)).printThirdLineLamp(eq(Mode.yellow));
		verify(berlinClockPrinter, times(3)).printThirdLineLamp(eq(Mode.red));
		
		verify(berlinClockPrinter, times(4)).printForthLineLamp(eq(Mode.yellow));
	}

	@Test
	public void print_120000() throws ParseException{
		berlinClock.print("12:00:00");
		verify(berlinClockPrinter, times(1)).printTopLamp(eq(Mode.yellow));
		
		verify(berlinClockPrinter, times(2)).printFirstLineLamp(eq(Mode.red));
		verify(berlinClockPrinter, times(2)).printFirstLineLamp(eq(Mode.off));
		
		verify(berlinClockPrinter, times(2)).printSecondLineLamp(eq(Mode.red));
		verify(berlinClockPrinter, times(2)).printSecondLineLamp(eq(Mode.off));
		
		verify(berlinClockPrinter, times(11)).printThirdLineLamp(eq(Mode.off));
		
		verify(berlinClockPrinter, times(4)).printForthLineLamp(eq(Mode.off));
	}

	@Test
	public void print_163215() throws ParseException{
		berlinClock.print("16:32:15");
		verify(berlinClockPrinter, times(1)).printTopLamp(eq(Mode.off));
		
		verify(berlinClockPrinter, times(3)).printFirstLineLamp(eq(Mode.red));
		verify(berlinClockPrinter, times(1)).printFirstLineLamp(eq(Mode.off));

		verify(berlinClockPrinter, times(1)).printSecondLineLamp(eq(Mode.red));
		verify(berlinClockPrinter, times(3)).printSecondLineLamp(eq(Mode.off));
		
		verify(berlinClockPrinter, times(2)).printThirdLineLamp(eq(Mode.red));
		verify(berlinClockPrinter, times(4)).printThirdLineLamp(eq(Mode.yellow));
		verify(berlinClockPrinter, times(5)).printThirdLineLamp(eq(Mode.off));
	
		verify(berlinClockPrinter, times(2)).printForthLineLamp(eq(Mode.yellow));
		verify(berlinClockPrinter, times(2)).printForthLineLamp(eq(Mode.off));
	}
	

	@Test
	public void print_000000() throws ParseException{
		berlinClock.print("00:00:00");
		verify(berlinClockPrinter, times(1)).printTopLamp(eq(Mode.yellow));
		verify(berlinClockPrinter, times(4)).printFirstLineLamp(eq(Mode.off));
		verify(berlinClockPrinter, times(4)).printSecondLineLamp(eq(Mode.off));
		verify(berlinClockPrinter, times(11)).printThirdLineLamp(eq(Mode.off));
		verify(berlinClockPrinter, times(4)).printForthLineLamp(eq(Mode.off));
	}	
}
