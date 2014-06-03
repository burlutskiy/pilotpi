package test;

interface BerlinClockPrinter {
	static enum Mode {
		red('R'), yellow('Y'), off('O');
		private char code;

		private Mode(char symbol) {
			this.code = symbol;
		}

		public char getCode() {
			return code;
		}
	}

	void printTopLamp(Mode colorMode);

	void printFirstLineLamp(Mode colorMode);

	void printSecondLineLamp(Mode colorMode);

	void printThirdLineLamp(Mode colorMode);

	void printForthLineLamp(Mode colorMode);
}
