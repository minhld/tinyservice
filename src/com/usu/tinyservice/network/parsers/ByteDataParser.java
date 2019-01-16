package com.usu.tinyservice.network.parsers;

import java.util.Arrays;

public class ByteDataParser implements IDataParser {

	@Override
	public Class<?> getDataClass() {
		return Byte.class;
	}

	@Override
	public Object loadObject(String path) {
		int length = Integer.parseInt(path) * 1000;
		byte[] newData = new byte[length];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = (byte) (Math.random() * 255);
		}
		return newData;
	}

	@Override
	public byte[] parseObjectToBytes(Object objData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseBytesToObject(byte[] byteData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getPartFromObject(Object objData, int taskIndex, int taskNumber) {
		byte[] data = (byte[]) objData;
		int dataSize = data.length;
		int taskSize = dataSize / taskNumber;
		int firstOffset = taskSize * taskIndex;
		int lastOffset = firstOffset + taskSize - 1;
		if ((lastOffset > (taskNumber - 1) * taskSize && lastOffset < dataSize - 1) ||
				lastOffset > dataSize - 1) {
			lastOffset = dataSize - 1;
		}
		return Arrays.copyOfRange(data, firstOffset, lastOffset);
	}

	@Override
	public Object createPlaceHolder(Object dataObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object copyPartToHolder(Object placeholderObj, byte[] partObj, int taskIndex, int taskNumber) {
		// TODO Auto-generated method stub
		return null;
	}

}
