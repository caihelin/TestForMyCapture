package com.example.testformycapture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MyQrActivity extends Activity
{
    protected int mScreenWidth;
    private ImageView imageView;
    private Bitmap logo;
    private static final int IMAGE_HALFWIDTH = 40;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr);
        imageView = (ImageView) findViewById(R.id.iv_qr_image);
        createImg();
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                MyQrActivity.this.finish();
            }
        });
    }

    public void createImg()
    {
        String codeInfo = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
        String str = formatter.format(curDate);
        codeInfo = str + "���ı�";
        logo = BitmapFactory.decodeResource(super.getResources(), R.drawable.icon_com);
        try
        {
            if (!codeInfo.equals(""))
            {
                // �����ַ������ɶ�ά��ͼƬ����ʾ�ڽ����ϣ��ڶ�������ΪͼƬ�Ĵ�С��350*350��
                Bitmap qrCodeBitmap = createCode(codeInfo, logo, BarcodeFormat.QR_CODE);
                imageView.setImageBitmap(qrCodeBitmap);
            } else
            {
                Toast.makeText(MyQrActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
            }
        } catch (WriterException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * * ���ɶ�ά��
     * 
     * @param string
     *            ��ά���а������ı���Ϣ
     * @param mBitmap
     *            logoͼƬ
     * @param format
     *            �����ʽ
     * @return Bitmap λͼ
     * @throws WriterException
     */
    public Bitmap createCode(String string, Bitmap mBitmap, BarcodeFormat format) throws WriterException
    {
        Matrix m = new Matrix();
        float sx = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getWidth();
        float sy = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getHeight();
        m.setScale(sx, sy);// ����������Ϣ
        // ��logoͼƬ��martix���õ���Ϣ����
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, false);
        MultiFormatWriter writer = new MultiFormatWriter();
        Hashtable hst = new Hashtable();
        hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");// �����ַ�����
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;// ���
        int screenHeight = dm.heightPixels;// �߶�
        BitMatrix matrix = writer.encode(string, format, screenWidth / 5 * 3, screenHeight / 5 * 2, hst);// ���ɶ�ά�������Ϣ
        int width = matrix.getWidth();// ����߶�
        int height = matrix.getHeight();// ������
        int halfW = width / 2;
        int halfH = height / 2;
        int[] pixels = new int[width * height];// �������鳤��Ϊ����߶�*�����ȣ����ڼ�¼������������Ϣ
        for (int y = 0; y < height; y++)
        {
            // ���п�ʼ��������
            for (int x = 0; x < width; x++)
            {
                // ������
                if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH && y < halfH + IMAGE_HALFWIDTH)
                {
                    // ��λ�����ڴ��ͼƬ��Ϣ
                    // ��¼ͼƬÿ��������Ϣ
                    pixels[y * width + x] = mBitmap.getPixel(x - halfW + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
                } else
                {
                    if (matrix.get(x, y))
                    {
                        // ����кڿ�㣬��¼��Ϣ
                        pixels[y * width + x] = 0xff000000;// ��¼�ڿ���Ϣ
                    }
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // ͨ��������������bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
