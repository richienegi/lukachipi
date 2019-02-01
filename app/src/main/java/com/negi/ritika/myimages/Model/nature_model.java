package com.negi.ritika.myimages.Model;


import com.negi.ritika.myimages.R;
import java.util.ArrayList;
import java.util.List;

public class nature_model
{
    private int imageid;
    private String title;

    public int getImageid()
    {
        return imageid;
    }

    public void setImageid(int imageid)
    {
        this.imageid=imageid;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title=title;
    }
    public static List<nature_model> getObjectList()
    {
        List<nature_model> dataList = new ArrayList<>();
        int image[] = getImages();
        String title[]=gettitle();
        for (int i=0; i<image.length;i++)
        {
            nature_model nm = new nature_model();
            nm.setImageid(image[i]);
            nm.setTitle(title[i]);
            dataList.add(nm);
        }
        return dataList;
    }
    private static int[] getImages()
    {
        int arr[]={R.drawable.wildlife,R.drawable.insects,R.drawable.flowers,R.drawable.landscape};
        return arr;
    }
    private static String[] gettitle()
    {
        String ar[]={"Wildlife","Insects","Flowers","Landscape"};
        return ar;
    }


}