package com.usu.tinyservice.network.parsers;

@SuppressWarnings("rawtypes")
public interface IDataParser {
	/**
     * get class definition of the data object which you use to exchange
     * to other devices
     *
     * @return
     */
	public Class getDataClass();

	/**
     * read data object from a local file or URL
     *
     * @param url
     * @return
     * @throws Exception
     */
	public Object loadObject(String url) throws Exception;

    /**
     * convert data object to byte array - serialize object to bytes
     *
     * @param objData
     * @return
     * @throws Exception
     */
    public byte[] parseObjectToBytes(Object objData) throws Exception;

    /**
     * convert from byte array to data object - deserialize byte array to object<br>
     * some object type cannot be deserialized, for example Bitmap
     *
     * @param byteData
     * @return
     * @throws Exception
     */
    public Object parseBytesToObject(byte[] byteData) throws Exception;

    /**
     * this function will get one small piece of a data object. the position
     * and size of the part depends on the firstOffset and the lastOffset.
     *
     * @param objData
     * @param firstOffset (percentage - integer) the first offset of the data piece (0 -> 100)
     * @param lastOffset (percentage - integer) the last offset of the data piece (0 -> 100)
     * @return
     */
    public byte[] getPartFromObject(Object objData, int firstOffset, int lastOffset);

    /**
     * create the placeholder to accumulate the results from other devices
     * sending back to requester
     *
     * @param dataObject
     * @return
     */
    public Object createPlaceHolder(Object dataObject);

    /**
     * merge the partial data object to the placeholder, in other words, final result.
     *
     * @param placeholderObj
     * @param partObj
     * @param firstOffset (percentage - integer) the first offset of the piece (0 -> 100)
     * @param lastOffset (percentage - integer) the last offset of the piece (0 -> 100)
     * @return
     */
    public Object copyPartToHolder(Object placeholderObj, byte[] partObj, int firstOffset, int lastOffset);

}
