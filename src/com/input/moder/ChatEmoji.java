package com.input.moder;


/**
 * 
 ******************************************
 * @文件名称	:  ChatEmoji.java
 * @文件描述	: 表情对象实体
 ******************************************
 */
public class ChatEmoji {

    /** 表情资源图片对应的ID */
    private int _id;

    /** 表情资源对应的文字描述 */
    private String _character;

    /** 表情资源的文件名 */
    private String _faceName;

    /** 表情资源图片对应的ID */
    public int getId() {
        return _id;
    }

    /** 表情资源图片对应的ID */
    public void setId(int id) {
        this._id=id;
    }

    /** 表情资源对应的文字描述 */
    public String getCharacter() {
        return _character;
    }

    /** 表情资源对应的文字描述 */
    public void setCharacter(String character) {
        this._character=character;
    }

    /** 表情资源的文件名 */
    public String getFaceName() {
        return _faceName;
    }

    /** 表情资源的文件名 */
    public void setFaceName(String faceName) {
        this._faceName=faceName;
    }
}
