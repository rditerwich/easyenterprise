/*
 * Copyright 2010 John Kozura
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bfr.client.selection;

import com.google.gwt.dom.client.*;

/**
* An end point of a range, represented as a text node and offset in to it.
* Does not support potential other types of selection end points.
* 
* @author John Kozura
*/
public class RangeEndPoint implements Comparable<RangeEndPoint>
{
    public static final short MOVE_CHARACTER 	= 1;
    public static final short MOVE_WORD		= 2;
    
    /**
    * The textNode containing the start/end of the selection.
    */
    private Text m_textNode;
    
    /**
    * The number of characters starting from the beginning of the textNode
    * where the selection begins/ends.
    */
    private int m_offset;
    
    /**
    * Create a range end point with nothing set
    */
    public RangeEndPoint()
    {
	super();
    }
    
    /**
    * Create a range end point at the start or end of an element.  The actual
    * selection will occur at the first/last text node within this element.
    * 
    * @param element element to create this end point in
    * @param start whether to make the end point at the start or the end
    */
    public RangeEndPoint(Element element, boolean start)
    {
	setElement(element, start);
    }
    
    /**
    * Create a range end point at the start or end of a text node
    * 
    * @param text text node this end point starts/end in
    * @param start whether to make the end point at the start or the end
    */
    public RangeEndPoint(Text text, boolean start)
    {
	this();
	setTextNode(text, start);
    }
    
    /**
    * Create a range end point with a text node and offset into it
    * 
    * @param text text node this end point occurs in
    * @param offset offset characters into the text node
    */
    public RangeEndPoint(Text text, int offset)
    {
	this();
	
	setTextNode(text);
	setOffset(offset);
    }
    
    /**
    * Clone a range end point
    * 
    * @param endPoint point to clone
    */
    public RangeEndPoint(RangeEndPoint endPoint)
    {
	this(endPoint.getTextNode(), endPoint.getOffset());
    }
    
    @Override
    public int compareTo(RangeEndPoint cmp)
    {
	Range thisRng = new Range(this);
	Range cmpRng = new Range(cmp);
	return thisRng.compareBoundaryPoint(cmpRng, Range.START_TO_START);
    }
    
    @Override
    public boolean equals(Object obj)
    {
	boolean res = false;
	
	try
	{
	    RangeEndPoint cmp = (RangeEndPoint)obj;
	    
	    res = (cmp == this) ||
	      	  ((cmp.getTextNode() == getTextNode()) &&
	    	   (cmp.getOffset() == getOffset()));
	}
	catch (Exception ex) {}
	
	return res;
    }
    
    /**
    * Get the offset into the text node
    * 
    * @return offset in characters
    */
    public int getOffset()
    {
	return m_offset;
    }
    
    /**
    * Get the string of the text node of this end point, either up to or 
    * starting from the offset:
    * 
    * "som|e text"
    *   true  : "e text"
    *   false : "som"
    * 
    * @param asStart whether to get the text as if this is a start point
    * @return the text before or after the offset
    */
    public String getString(boolean asStart)
    {
	String res = m_textNode.getData();
	return asStart ? res.substring(m_offset) : res.substring(0, m_offset);
    }
    
    /**
    * Get the text node of this end point
    * 
    * @return the text node
    */
    public Text getTextNode()
    {
	return m_textNode;
    }
    
    @SuppressWarnings("deprecation")
    public boolean isSpace(Character check)
    {
	return Character.isSpace(check);
    }
    
    /**
    * TODO NOT IMPLEMENTED YET
    * Move the end point forwards or backwards by one unit of type, such as
    * by a word.  
    * 
    * @param forward true if moving forward
    * @param type what unit to move by, ie MOVE_CHARACTER or MOVE_WORD
    * @param count how many of these to move by
    * @return how far this actually moved
    */
    public int move(boolean forward, short type, int count)
    {
	assert(false);
	
	return 0;
    }
    
    /**
    * Set the range end point at the start or end of an element.  The actual
    * selection will occur at the first/last text node within this element.
    * 
    * @param element element to set this end point in
    * @param start whether to make the end point at the start or the end
    */
    public void setElement(Element element, boolean start)
    {
	Text text = Range.getAdjacentTextElement(element, element, 
	                                         start, false);
	if (text != null)
	{
	    setTextNode(text, start);
	}
    }
    
    /**
    * Set the offset into the text node
    * 
    * @param offset offset in characters
    */
    public void setOffset(int offset) 
    {
	m_offset = offset;
    }
    
    /**
    * Set the text node this end point occurs in
    * 
    * @param text text node this end point occurs in
    */
    public void setTextNode(Text textNode)
    {
	m_textNode = textNode;
    }
    
    /**
    * Set this range end point at the start or end of a text node
    * 
    * @param text text node this end point starts/end in
    * @param start whether to make the end point at the start or the end
    */
    public void setTextNode(Text textNode, boolean start)
    {
	setTextNode(textNode);
	setOffset(start ? 0 : textNode.getLength());
    }
    
    /**
    * Get the text of this with a "|" at the offset
    * 
    * @return a string representation of this endpoint
    */
    @Override
    public String toString()
    {
	return getString(false) + "|" + getString(true);
    }
}
