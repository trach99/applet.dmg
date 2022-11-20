package Demo_Applet_STK;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import sim.toolkit.EnvelopeHandler;
import sim.toolkit.ProactiveHandler;
import sim.toolkit.ProactiveResponseHandler;
import sim.toolkit.ToolkitConstants;
import sim.toolkit.ToolkitException;
import sim.toolkit.ToolkitInterface;
import sim.toolkit.ToolkitRegistry;

/**
 * @author HarikrushnG
 *
 */
public class Demo_Applet_STK extends Applet implements ToolkitInterface, ToolkitConstants
{
	/** ToolkitRegistry Object */
	private ToolkitRegistry toolkitRegistry;
	
	/** Main Menu Id array */
	private static byte[] menuId ={(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00};
	
	// Register
	private static final byte[] mainMenu_1 = {(byte) 'C', (byte) 'a', (byte) 'l', (byte) 'l', (byte) ' ', (byte) 'c', (byte) 'e', (byte) 'n', (byte) 't', (byte) 'e', (byte) 'r'};
	// Login
	private static final byte[] mainMenu_2 = {(byte) 'B', (byte) 'a', (byte) 'l', (byte) 'a', (byte) 'n', (byte) 'c', (byte) 'e', (byte) ' ', (byte) 'e', (byte) 'n', (byte) 'q', (byte) 'u', (byte) 'i', (byte) 'r', (byte) 'y'};

	// Enter your name
	private static final byte[] getInput_Name = {(byte) 'E', (byte) 'n', (byte) 't', (byte) 'e', (byte) 'r', (byte) ' ', (byte) 'y', (byte) 'o', (byte) 'u', (byte) 'r', (byte) ' ', (byte) 'N', (byte) 'a', (byte) 'm', (byte) 'e'};
	// Enter the phone number
	private static final byte[] getInput_PhoneNumber = {(byte) 'E', (byte) 'n', (byte) 't', (byte) 'e', (byte) 'r', (byte) ' ', (byte) 'y', (byte) 'o', (byte) 'u', (byte) 'r', (byte) ' ', (byte) 'M', (byte) 'o', (byte) 'b', (byte) 'i', (byte) 'l', (byte) 'e', (byte) ' ', (byte) 'n', (byte) 'u', (byte) 'm', (byte) 'b', (byte) 'e', (byte) 'r'};
	// Enter the amount
	private static final byte[] getInput_Amount = {(byte) 'E', (byte) 'n', (byte) 't', (byte) 'e', (byte) 'r', (byte) ' ', (byte) 't', (byte) 'h', (byte) 'e', (byte) ' ', (byte) 'a', (byte) 'm', (byte) 'o', (byte) 'u', (byte) 'n', (byte) 't'};
	
	private static final byte[] sendUSSD_AlphaId = {(byte) 'S', (byte) 'e', (byte) 'n', (byte) 'd', (byte) 'i', (byte) 'n', (byte) 'g', (byte) ' ', (byte) 'r', (byte) 'e', (byte) 'q', (byte) 'u', (byte) 'e', (byte) 's', (byte) 't', (byte) ' ', (byte) '.', (byte) '.', (byte) '.'};
	
	private byte[] setUpCall = JCSystem.makeTransientByteArray((short) 3,JCSystem.CLEAR_ON_RESET);
	private byte[] sendUSSD = JCSystem.makeTransientByteArray((short) 23,JCSystem.CLEAR_ON_RESET);
	private byte[] sevenBitPackingSendUSSD = JCSystem.makeTransientByteArray((short) 23,JCSystem.CLEAR_ON_RESET);
	//private byte[] sevenBitUnpackingSendUSSD = JCSystem.makeTransientByteArray((short) 255,JCSystem.CLEAR_ON_RESET);
	private byte[] ussdResponseData = JCSystem.makeTransientByteArray((short) 255,JCSystem.CLEAR_ON_RESET);
	
	private static short ussdResponseLength = 0;
	private static byte ussdDcs = 0;
	/**
	 * Constructor.
	 */
	private Demo_Applet_STK()
	{
		// Register Application
		register();
		
		// Register to the SIM Toolkit Framework
		toolkitRegistry = ToolkitRegistry.getEntry();
		
		menuId[0] = toolkitRegistry.initMenuEntry(mainMenu_1, (byte)0x00, (short)mainMenu_1.length, (byte)0x00, false, (byte)0x00, (byte)0x00);
		menuId[1] = toolkitRegistry.initMenuEntry(mainMenu_2, (byte)0x00, (short)mainMenu_2.length, (byte)0x00, false, (byte)0x00, (byte)0x00);
		menuId[2] = toolkitRegistry.initMenuEntry(mainMenu_3, (byte)0x00, (short)mainMenu_3.length, (byte)0x00, false, (byte)0x00, (byte)0x00);
		menuId[3] = toolkitRegistry.initMenuEntry(mainMenu_4, (byte)0x00, (short)mainMenu_4.length, (byte)0x00, false, (byte)0x00, (byte)0x00);
		menuId[4] = toolkitRegistry.initMenuEntry(mainMenu_5, (byte)0x00, (short)mainMenu_5.length, (byte)0x00, false, (byte)0x00, (byte)0x00);
		menuId[5] = toolkitRegistry.initMenuEntry(mainMenu_6, (byte)0x00, (short)mainMenu_6.length, (byte)0x00, false, (byte)0x00, (byte)0x00);
		menuId[6] = toolkitRegistry.initMenuEntry(mainMenu_7, (byte)0x00, (short)mainMenu_7.length, (byte)0x00, false, (byte)0x00, (byte)0x00);
		menuId[7] = toolkitRegistry.initMenuEntry(mainMenu_8, (byte)0x00, (short)mainMenu_8.length, (byte)0x00, false, (byte)0x00, (byte)0x00);
	}

	/**
	 * This method is used to install the application on the card.
	 * @param bArray 
	 * @param bOffset 
	 * @param bLength 
	 * @throws ISOException 
	 * 
	 * @param1 bArray 
	 * 					- The array containing installation parameters.
	 * @param2 bOffset 
	 * 					- The starting offset in bArray.
	 * @param3 bLength 
	 * 					- The length in bytes of the parameter data in bArray.
	 */
	public static void install( byte[] bArray,
								short bOffset,
								byte bLength ) throws ISOException
	{
		// New instance of LifeTimeIndicator
		new Demo_Applet_STK();
	}
	
	/* (non-Javadoc)
	 * @see sim.toolkit.ToolkitInterface#processToolkit(byte)
	 */
	public void processToolkit( byte event )
											throws ToolkitException
	{
		switch ( event )
		{
			case EVENT_MENU_SELECTION:
			{
				Util.arrayFillNonAtomic( sendUSSD, (short)0, (short)sendUSSD.length, (byte)0x00 );
				Util.arrayFillNonAtomic( sevenBitPackingSendUSSD, (short)0, (short)sevenBitPackingSendUSSD.length, (byte)0x00 );
				Util.arrayFillNonAtomic( ussdResponseData, (short)0, (short)ussdResponseData.length, (byte)0x00 );
				
				EnvelopeHandler envHdlr = EnvelopeHandler.getTheHandler();
			
				byte result = envHdlr.getItemIdentifier();
			
				if(menuId[0] == result)
				{
					//1333
					setUpCall[0] = (byte)0x81;
					setUpCall[1] = (byte)0x31;
					setUpCall[2] = (byte)0x33;
					
					setUpCall(setUpCall,(byte)3);
				}
				else if(menuId[1] == result)
				{
					//*138#
					sendUSSD[0] = (byte)'*';
					sendUSSD[1] = (byte)'1';
					sendUSSD[2] = (byte)'3';
					sendUSSD[3] = (byte)'8';
					sendUSSD[4] = (byte)'#';
					
					sevenBitPackingSendUSSD [0] = (byte)0x0F;
					short resultLength = sevenBitPacking(sendUSSD, (short) 0, sevenBitPackingSendUSSD, (short) 1, (short)5);
					
					sendUSSD( sevenBitPackingSendUSSD, (short) ( resultLength + 1 ) );
				}
				else if(menuId[2] == result)
				{
					result = getInput(getInput_RechargePin,(short)getInput_RechargePin.length, (short)16, (short)16);
					
					if(result <= (byte)0x09)
					{
						sendUSSD[0] = (byte)'*';
						sendUSSD[1] = (byte)'1';
						sendUSSD[2] = (byte)'0';
						sendUSSD[3] = (byte)'4';
						sendUSSD[4] = (byte)'*';
						
						ProactiveResponseHandler.getTheHandler().copyTextString(sendUSSD,(short)5);
						
						sendUSSD[21] = (byte)'#';
						
						sevenBitPackingSendUSSD [0] = (byte)0x0F;
						short resultLength = sevenBitPacking(sendUSSD, (short) 0, sevenBitPackingSendUSSD, (short) 1, (short)22);
						
						sendUSSD( sevenBitPackingSendUSSD, (short) ( resultLength + 1 ) );
					}
				}
				else if(menuId[3] == result)
				{
					while(true)
    				{
    					result = getInput(getInput_PhoneNumber,(short)getInput_PhoneNumber.length, (short)8, (short)8);
    					
    					if(result <= (byte)0x09)
    					{
    						sendUSSD[0] = (byte)'*';
    						sendUSSD[1] = (byte)'1';
    						sendUSSD[2] = (byte)'3';
    						sendUSSD[3] = (byte)'7';
    						sendUSSD[4] = (byte)'*';
    						
    						ProactiveResponseHandler.getTheHandler().copyTextString(sendUSSD,(short)5);
    						
    						sendUSSD[13] = (byte)'*';
    						
    						result = getInput(getInput_Amount,(short)getInput_Amount.length, (short)1, (short)3);
    						
    						if(result <= (byte)0x09)
    						{
    							sendUSSD[15] = (byte)'#';
    							sendUSSD[16] = (byte)'#';
    							sendUSSD[17] = (byte)'#';
    							byte lenght = (byte)ProactiveResponseHandler.getTheHandler().copyTextString(sendUSSD,(short)14);
    							
    							sevenBitPackingSendUSSD [0] = (byte)0x0F;
    							short resultLength = sevenBitPacking(sendUSSD, (short) 0, sevenBitPackingSendUSSD, (short) 1, (short)(lenght+1));
    							
    							sendUSSD( sevenBitPackingSendUSSD, (short) ( resultLength + 1 ) );
    							return;
    						}
    						else if(RES_CMD_PERF_BACKWARD_MOVE_REQ == result)
    						{
    							continue;
    						}
    						else
    						{
    							return;
    						}
    					}
    					else
    					{
    						return;
    					}
    				}
				}
				else if(menuId[4] == result)
				{
					ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
					proHdlr.initGetInput((byte)0x00, DCS_8_BIT_DATA, getInput_PhoneNumber, (short)0, (short)getInput_PhoneNumber.length, (short)8, (short)8);
					
					result = proHdlr.send();
					
					if(result <= (byte)0x09)
					{
						sendUSSD[0] = (byte)'*';
						sendUSSD[1] = (byte)'1';
						sendUSSD[2] = (byte)'0';
						sendUSSD[3] = (byte)'1';
						sendUSSD[4] = (byte)'*';
						
						ProactiveResponseHandler.getTheHandler().copyTextString(sendUSSD,(short)5);
						
						sendUSSD[13] = (byte)'#';
						
						sevenBitPackingSendUSSD [0] = (byte)0x0F;
						short resultLength = sevenBitPacking(sendUSSD, (short) 0, sevenBitPackingSendUSSD, (short) 1, (short)14);
						
						sendUSSD( sevenBitPackingSendUSSD, (short) ( resultLength + 1 ) );
					}
				}
				else if(menuId[5] == result)
				{
					//1301
					setUpCall[0] = (byte)0x81;
					setUpCall[1] = (byte)0x31;
					setUpCall[2] = (byte)0x10;
					
					setUpCall(setUpCall,(byte)3);
				}
				else if(menuId[6] == result)
				{
					//1314
					setUpCall[0] = (byte)0x81;
					setUpCall[1] = (byte)0x31;
					setUpCall[2] = (byte)0x41;
					
					setUpCall(setUpCall,(byte)3);
				}
				else if(menuId[7] == result)
				{
					sendUSSD[0] = (byte)'*';
					sendUSSD[1] = (byte)'1';
					sendUSSD[2] = (byte)'7';
					sendUSSD[3] = (byte)'3';
					sendUSSD[4] = (byte)'#';
					
					sevenBitPackingSendUSSD [0] = (byte)0x0F;
					short resultLength = sevenBitPacking(sendUSSD, (short) 0, sevenBitPackingSendUSSD, (short) 1, (short)5);
					
					sendUSSD( sevenBitPackingSendUSSD, (short) ( resultLength + 1 ) );
				}
			}
			break;
		}
	}

	private byte getInput( byte[] getinputString, short length, short min, short max )
	{
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.initGetInput((byte)0x00, DCS_8_BIT_DATA, getinputString, (short)0, length, min, max);
		
		return proHdlr.send();
	}

	/**
	 * @param setUpCall
	 * @param length
	 */
	private void setUpCall(byte[] setUpCall, short length)
	{
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.init(PRO_CMD_SET_UP_CALL, (byte) 0x00, DEV_ID_NETWORK);
		proHdlr.appendTLV(TAG_ADDRESS, setUpCall, (short) 0, length);
		proHdlr.send();
	}

	/**
	 * @param sendUSSD
	 * @param resultLength
	 */
	private void sendUSSD(byte[] sevenBitPackingSendUSSD, short resultLength)
	{
		ProactiveHandler proHdlr = ProactiveHandler.getTheHandler();
		proHdlr.init(PRO_CMD_SEND_USSD, (byte)0x00, DEV_ID_NETWORK);
		proHdlr.appendTLV(TAG_ALPHA_IDENTIFIER, sendUSSD_AlphaId, (short)0x00, (short)sendUSSD_AlphaId.length);
		proHdlr.appendTLV(TAG_USSD_STRING, sevenBitPackingSendUSSD, (short)0x00, resultLength);
		byte result = proHdlr.send();
		
		if( result <= (byte)0x09 )
		{
			try
			{
				ProactiveResponseHandler proRespHdlr = ProactiveResponseHandler.getTheHandler();
				
				ussdResponseLength = proRespHdlr.getTextStringLength();
				proRespHdlr.copyTextString(ussdResponseData, (short) 0);
				ussdDcs = proRespHdlr.getTextStringCodingScheme();
				
				proHdlr.initDisplayText( (byte)0x81, ussdDcs, ussdResponseData, (short)0, ussdResponseLength );
				proHdlr.send();
			}
			catch(ToolkitException tkEx)
			{
				return;
			}
		}
	}
	
	protected short sevenBitPacking( byte[] src, short srcOff, byte[] dest, short destOff, short length )
	{
		// set carriage return at the end
		if ( (byte) ( length % 8 ) == 7 )
		{
			src[ length++ ] = 0x0D;
		}

		byte remainder = (byte) ( length % 8 );
		byte octets = (byte) ( length / 8 );
		short resultLength = (short) ( octets * 7 + remainder );

		// clear result buffer
		Util.arrayFillNonAtomic( dest, destOff, resultLength, (byte)0x00 );

		for ( byte k = 1; k <= octets; k++ )
		{
			for ( byte i = 0, j = 7, b = (byte)0x80; i < 7; i++, j-- )
			{
				dest[ (short) ( destOff + i ) ] = (byte) ( ( src[ (short) ( srcOff + i ) ] >> i ) | ( ( src[ (short) ( srcOff + ( i + 1 ) ) ] << j ) & b ) );
				b |= ( 1 << ( j - 1 ) );
			}
			destOff += 7;
			srcOff += 8;
		}

		// transformation of remaining bytes
		for ( byte i = 0, j = 7, b = (byte)0x80; i < remainder; i++, j-- )
		{
			if ( ( i + 1 ) < remainder )
			{
				dest[ (short) ( destOff + i ) ] = (byte) ( ( src[ (short) ( srcOff + i ) ] >>> i ) | ( ( src[ (short) ( srcOff + ( i + 1 ) ) ] << j ) & b ) );
				b |= ( 1 << ( j - 1 ) );
			}
			else
			{
				dest[ (short) ( destOff + i ) ] = (byte) ( src[ (short) ( srcOff + i ) ] >>> i );
			}
		}
		return resultLength;
	} // sevenBitPacking()
	
	/* (non-Javadoc)
	 * @see javacard.framework.Applet#process(javacard.framework.APDU)
	 */
	public void process( APDU apdu )
									throws ISOException
	{
		
	}
}
