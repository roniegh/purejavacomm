/*
 * Copyright (c) 2011, Kustaa Nyholm / SpareTimeLabs
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list 
 * of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this 
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *  
 * Neither the name of the Kustaa Nyholm or SpareTimeLabs nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package purejavacomm.testsuite;

import java.util.Arrays;

import purejavacomm.*;

public class Test8 extends TestBase {
	static void run() throws Exception {
		try {
			begin("Test8 - parity etc");
			openPort();
			int[] parity = { SerialPort.PARITY_NONE, SerialPort.PARITY_ODD, SerialPort.PARITY_EVEN };
			int[] stopbits = { SerialPort.STOPBITS_1, SerialPort.STOPBITS_1_5, SerialPort.STOPBITS_2 };// ,
			// SerialPort.STOPBITS_1_5
			// };
			int[] databits = { SerialPort.DATABITS_8, SerialPort.DATABITS_7, SerialPort.DATABITS_6, SerialPort.DATABITS_5 };
			int[] datamask = { 0x1F, 0x3F, 0x7F, 0xFF };
			System.out.println();
			int tn = 0;
			for (int ppi = 0; ppi < parity.length; ppi++) {
				for (int sbi = 0; sbi < stopbits.length; sbi++) {
					for (int dbi = 0; dbi < databits.length; dbi++) {

						m_Port.enableReceiveTimeout(10000);
						m_Port.enableReceiveThreshold(256);
						try {
							String db = "?";
							switch (databits[dbi]) {
								case SerialPort.DATABITS_5:
									db = "5";
									break;
								case SerialPort.DATABITS_6:
									db = "6";
									break;
								case SerialPort.DATABITS_7:
									db = "7";
									break;
								case SerialPort.DATABITS_8:
									db = "8";
									break;
							}

							String sb = "?";
							switch (stopbits[sbi]) {
								case SerialPort.STOPBITS_1:
									sb = "1";
									break;
								case SerialPort.STOPBITS_1_5:
									sb = "1.5";
									break;
								case SerialPort.STOPBITS_2:
									sb = "2";
									break;
							}

							String pb = "?";
							switch (parity[ppi]) {
								case SerialPort.PARITY_EVEN:
									pb = "E";
									break;
								case SerialPort.PARITY_ODD:
									pb = "O";
									break;
								case SerialPort.PARITY_MARK:
									pb = "M";
									break;
								case SerialPort.PARITY_SPACE:
									pb = "S";
									break;
								case SerialPort.PARITY_NONE:
									pb = "N";
									break;
							}
							tn++;
							begin("Test8." + tn + " databits=" + db + " stopbits=" + sb + " parity=" + pb);
							m_Port.setSerialPortParams(19200, databits[dbi], stopbits[sbi], parity[ppi]);
							sleep(100);
							byte[] sent = new byte[256];
							byte[] rcvd = new byte[256];
							for (int i = 0; i < 256; i++)
								sent[i] = (byte) (i & datamask[dbi]);
							m_Out = m_Port.getOutputStream();
							m_In = m_Port.getInputStream();
							long t0 = System.currentTimeMillis();
							m_Out.write(sent);

							int n = 0;
							while ((n += m_In.read(rcvd, n, 256 - n)) < 256)
								;

							if (n != sent.length)
								fail("was expecting %d characters got %d", sent.length, n);
							for (int i = 0; i < 256; ++i) {
								if (sent[i] != rcvd[i])
									fail("failed: transmit '0x%02X' != receive'0x%02X'", sent[i], rcvd[i]);
							}
							if (n < 256)
								fail("did not receive all 256 chars, got %d", n);
							finishedOK();
						} catch (UnsupportedCommOperationException e) {
							finishedOK(" NOT SUPPORTED");
						}
						// sleep(1);
					}
				}
			}

		} finally {
			closePort();
		}

	}
}
