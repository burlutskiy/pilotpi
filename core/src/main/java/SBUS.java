import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;


public class SBUS {
	private boolean sbus_failsafe = false;
	
	int[] channels = new int[24];
	final class DataListener implements SerialDataListener {
		@Override
		public void dataReceived(SerialDataEvent event) {
			String data = event.getData();
			if(data.length() == 24){
				byte[] buffer = data.getBytes();
				channels[0]  = ((buffer[1]    |buffer[2]<<8)                 & 0x07FF);
				channels[1]  = ((buffer[2]>>3 |buffer[3]<<5)                 & 0x07FF);
				channels[2]  = ((buffer[3]>>6 |buffer[4]<<2 |buffer[5]<<10)  & 0x07FF);
				channels[3]  = ((buffer[5]>>1 |buffer[6]<<7)                 & 0x07FF);
				channels[4]  = ((buffer[6]>>4 |buffer[7]<<4)                 & 0x07FF);
				channels[5]  = ((buffer[7]>>7 |buffer[8]<<1 |buffer[9]<<9)   & 0x07FF);
				channels[6]  = ((buffer[9]>>2 |buffer[10]<<6)                & 0x07FF);
				channels[7]  = ((buffer[10]>>5|buffer[11]<<3)                & 0x07FF);
				channels[8]  = ((buffer[12]   |buffer[13]<<8)                & 0x07FF);
				channels[9]  = ((buffer[13]>>3|buffer[14]<<5)                & 0x07FF);
				channels[10] = ((buffer[14]>>6|buffer[15]<<2|buffer[16]<<10) & 0x07FF);
				channels[11] = ((buffer[16]>>1|buffer[17]<<7)                & 0x07FF);
				channels[12] = ((buffer[17]>>4|buffer[18]<<4)                & 0x07FF);
				channels[13] = ((buffer[18]>>7|buffer[19]<<1|buffer[20]<<9)  & 0x07FF);
				channels[14] = ((buffer[20]>>2|buffer[21]<<6)                & 0x07FF);
				channels[15] = ((buffer[21]>>5|buffer[22]<<3)                & 0x07FF);

				channels[16] = (((buffer[23]) & 0x0001) == 1) ? 2047 : 0;
				channels[16] = (((buffer[23] >> 1) & 0x0001) == 1) ? 2047 : 0;

				sbus_failsafe = ((buffer[23] >> 3) & 0x0001) == 1;
			} else {
				//ignore
				return;
			}
		}
	}

	void init(){
        Serial serial = SerialFactory.createInstance();
        serial.addListener(new DataListener());
        serial.open("/dev/ttyAMA0", 100000);
	}
	
	int getChannel(byte channel){
		return channels[channel];
	}
	
	void update(){
		
	}
	
	boolean isFailsafe(){
		return sbus_failsafe;
	}
}
