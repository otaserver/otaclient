/**
 * ʵ�������Զ�����
 * �Զ���ҳ
 */
package com.otaserver.client.utils;

import java.util.Vector;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.KeyEvent;

public class TextUtil
{
	int		m_iTextPosX;	//���Ƶ�x��
	int		m_iTextPosY;	//���Ƶ�y��
	int		m_iTextWidth;	//���ƿ��
	int		m_iTextHeight;	//���Ƹ߶�

	int		m_iFontHeight;	//����߶�

	int		m_ipageLineNum;	//ÿһҳ��ʾ������

	int		m_iTextBGColor; // ������ɫ
	int		m_iTextColor;	// ������ɫ
	int		m_iAlpha;		//Alphaֵ

	int		m_iRealLine;	// �ַ���ʵ������
	int		m_iCurLine;		//��ǰ��

	String	m_strText;		

	Vector	m_String;

	Paint	m_paint;

	int		m_iTextSize;


	public TextUtil()
	{
		m_paint = new Paint();
		m_String = new Vector();
	}


	public TextUtil(String strText, int x, int y, int w, int h, int bgcolor, int txetcolor, int a, int iTextSize)
	{
		m_paint = new Paint();
		m_String = new Vector();

		m_strText = strText;

		m_iTextPosX = x;
		m_iTextPosY = y;
		m_iTextWidth = w;
		m_iTextHeight = h;

		m_iTextBGColor = bgcolor;
		m_iTextColor = txetcolor;

		m_iTextSize = iTextSize;

		m_iAlpha = a;

	}

	/**
	 * ��ʼ��
	 * @param strText	Ҫ��ʾ���ַ�
	 * @param x			x
	 * @param y			y
	 * @param w			w
	 * @param h			h	
	 * @param bgcolor	������ɫ
	 * @param txetcolor	���ֵ���ɫ
	 * @param a			Alpha
	 * @param iTextSize	�����С
	 */
	public void InitText(String strText, int x, int y, int w, int h, int bgcolor, int txetcolor, int a, int iTextSize)
	{
		m_iCurLine = 0;
		m_ipageLineNum = 0;
		m_iRealLine = 0;
		m_strText = "";
		m_iTextPosX = 0;
		m_iTextPosY = 0;
		m_iTextWidth = 0;
		m_iTextHeight = 0;
		m_iTextBGColor = 0;
		m_iTextColor = 0;
		m_iTextSize = 0;
		m_iAlpha = 0;

		m_String.clear();

		SetText(strText);
		SetRect(x, y, w, h);
		SetBGColor(bgcolor);
		SetTextColor(txetcolor);
		SetFontSize(iTextSize);
		SetAlpha(a);

		SetPaint();

		GetTextIfon();
	}

	/**
	 * ����Alpha
	 * @param a	Alphaֵ
	 */
	public void SetAlpha(int a)
	{
		m_iAlpha = a;
	}

	/**
	 * ��Paint���Ե�����
	 */
	public void SetPaint()
	{
		m_paint.setARGB(m_iAlpha, Color.red(m_iTextColor), Color.green(m_iTextColor), Color.blue(m_iTextColor));
		m_paint.setTextSize(m_iTextSize);
	}

	/**
	 * ��������ߴ�
	 * @param iTextSize
	 */
	public void SetFontSize(int iTextSize)
	{
		m_iTextSize = iTextSize;
	}

	/**
	 * ������ʾ�ı�������
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void SetRect(int x, int y, int w, int h)
	{
		m_iTextPosX = x;
		m_iTextPosY = y;
		m_iTextWidth = w;
		m_iTextHeight = h;
	}

	/**
	 * ���ñ�����ɫ
	 * @param bgcolor
	 */
	public void SetBGColor(int bgcolor)
	{
		m_iTextBGColor = bgcolor;
	}

	/**
	 * ����������ɫ
	 * @param txetcolor
	 */
	public void SetTextColor(int txetcolor)
	{
		m_iTextColor = txetcolor;
	}

	/**
	 * ɫ����Ҫ��ʾ���ַ�
	 * @param strText
	 */
	public void SetText(String strText)
	{
		m_strText = strText;
	}

	/**
	 * �õ��ַ����Ϣ
	 * ����������ҳ�����Ϣ
	 * �ڲ�����
	 */
	public void GetTextIfon()
	{
		char ch;
		int w = 0;
		int istart = 0;
		FontMetrics fm = m_paint.getFontMetrics();
		
		m_iFontHeight = (int) Math.ceil(fm.descent - fm.top) + 2;

		m_ipageLineNum = m_iTextHeight / m_iFontHeight;

		for (int i = 0; i < m_strText.length(); i++)
		{
			ch = m_strText.charAt(i);
			float[] widths = new float[1];
			String srt = String.valueOf(ch);
			m_paint.getTextWidths(srt, widths);

			if (ch == '\n')
			{
				m_iRealLine++;
				m_String.addElement(m_strText.substring(istart, i));
				istart = i + 1;
				w = 0;
			}
			else
			{
				w += (int) (Math.ceil(widths[0]));
				if (w > m_iTextWidth)
				{
					m_iRealLine++;
					m_String.addElement(m_strText.substring(istart, i));
					istart = i;
					i--;
					w = 0;
				}
				else
				{
					if (i == (m_strText.length() - 1))
					{
						m_iRealLine++;
						m_String.addElement(m_strText.substring(istart, m_strText.length()));
					}
				}
			}
		}
	}

	/**
	 * �����ַ�
	 * @param canvas
	 */
	public void DrawText(Canvas canvas)
	{
		for (int i = m_iCurLine, j = 0; i < m_iRealLine; i++, j++)
		{
			if (j > m_ipageLineNum)
			{
				break;
			}
			canvas.drawText((String) (m_String.elementAt(i)), m_iTextPosX, m_iTextPosY + m_iFontHeight * j, m_paint);
		}
	}

	/**
	 * ��ҳ�Ȱ�����
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean KeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
		{
			if (m_iCurLine > 0)
			{
				m_iCurLine--;
			}
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
		{
			if ((m_iCurLine + m_ipageLineNum) < (m_iRealLine - 1))
			{
				m_iCurLine++;
			}
		}
		return false;
	}
}
