package test;

class SystemOutPrinter implements BerlinClockPrinter {
	byte currentLine = -1;

	@Override
	public void printFirstLineLamp(Mode colorMode) {
		if (currentLine != 1) {
			System.out.println();
			currentLine = 1;
		}
		System.out.print(colorMode.getCode());
	}

	@Override
	public void printSecondLineLamp(Mode colorMode) {
		if (currentLine != 2) {
			System.out.println();
			currentLine = 2;
		}
		System.out.print(colorMode.getCode());
	}

	@Override
	public void printThirdLineLamp(Mode colorMode) {
		if (currentLine != 3) {
			System.out.println();
			currentLine = 3;
		}
		System.out.print(colorMode.getCode());
	}

	@Override
	public void printForthLineLamp(Mode colorMode) {
		if (currentLine != 4) {
			System.out.println();
			currentLine = 4;
		}
		System.out.print(colorMode.getCode());
	}

	@Override
	public void printTopLamp(Mode colorMode) {
		if (currentLine != 0) {
			System.out.println();
			currentLine = 0;
		}
		System.out.print(colorMode.getCode());
	}

}
