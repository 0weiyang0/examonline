package com.duyi.examonline.common.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Data@AllArgsConstructor
public class PageVO<T> implements Serializable {
/**
 * curr  要访问的page
 * data   从数据库中查出来的数据   比如教师信息，学生信息
 * condition   查询条件
 */
    private int curr ;
    private int rows ;
    private long total ;
    private int max ;
    private int start ;
    private int end ;
    private List<T> data ;
    private Map<String,Object> condition ;
    public int getStartPage(){
        if(curr > 2 && curr + 2 < max){
            return curr-2 ;
        }else if(curr <= 2){
            return 1;
        }else{
            //curr + 2>=max
            return max-5+1;
        }
    }
    public int getEndPage(){
        if(curr > 2 && curr + 2 < max){
            return curr+2 ;
        }else if(curr <= 2 && max > 5){
            return 5 ;
        }else{
            //curr + 2>=max
            return max ;
        }
    }

}
