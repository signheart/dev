package com.keyue.qlm.util;
import java.io.BufferedReader; 
import java.io.File; 
import java.io.FileReader; 
import java.util.ArrayList; 
import java.util.List; 
  
/** 
 * ********************************************** 
 * @description ����Դ���루src����������������� 
 *     ��ּ����src�������ļ���װ��list,��ɸѡ���ļ������ļ����б�����ȡ 
 * @author gumutianqi 
 * @date 2011-05-30   2:00:12 PM 
 * @version 1.0 
 *********************************************** 
 */
public class LineCounter { 
    List<File> list = new ArrayList<File>(); 
    int linenumber = 0; 
      
    FileReader fr = null; 
    BufferedReader br = null; 
  
    public void counter(String projectName) { 
//        String path = System.getProperty("user.dir"); 
        String path = LineCounter.class.getResource("/").getPath();  // ͬ�¸�path 
        path = path.substring(0, path.length() - 24) + projectName + "/src"; 
        System.out.println(path); 
        File file = new File(path); 
        File files[] = null; 
        files = file.listFiles(); 
        addFile(files); 
        isDirectory(files); 
        readLinePerFile(); 
        System.out.println("Totle:" + linenumber + "��"); 
    } 
  
    // �ж��Ƿ���Ŀ¼ 
    public void isDirectory(File[] files) { 
        for (File s : files) { 
            if (s.isDirectory()) { 
                File file[] = s.listFiles(); 
                addFile(file); 
                isDirectory(file); 
                continue; 
            } 
        } 
    } 
  
    //��src�������ļ���֯��list 
    public void addFile(File file[]) { 
        for (int index = 0; index < file.length; index++) { 
            list.add(file[index]); 
            // System.out.println(list.size()); 
        } 
    } 
      
    //��ȡ�ǿհ��� 
    public void readLinePerFile() { 
        try { 
            for (File s : list) { 
                int yuan = linenumber; 
                if (s.isDirectory()) { 
                    continue; 
                } 
                fr = new FileReader(s); 
                br = new BufferedReader(fr); 
                String i = ""; 
                while ((i = br.readLine()) != null) { 
                    if (isBlankLine(i)) 
                        linenumber++; 
                } 
                System.out.print(s.getName()); 
                System.out.println("\t\t��" + (linenumber - yuan) + "��"); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            if (br != null) { 
                try { 
                    br.close(); 
                } catch (Exception e) { 
                } 
            } 
            if (fr != null) { 
                try { 
                    fr.close(); 
                } catch (Exception e) { 
                } 
            } 
        } 
    } 
  
    //�Ƿ��ǿ��� 
    public boolean isBlankLine(String i) { 
        if (i.trim().length() == 0) { 
            return false; 
        } else { 
            return true; 
        } 
    } 
      
    public static void main(String args[]) { 
        LineCounter lc = new LineCounter(); 
        String projectName = "";     //���ﴫ�������Ŀ���� 
        System.out.println("1");
        lc.counter(projectName); 
        System.out.println("2");
    } 
}