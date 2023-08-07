package json.dto;

import lombok.Data;

import java.util.List;

/***
 * Created by zhengyu.shang on 2021/11/22.
 */
@Data
public class Words {
    private int id;
    private String wordhead;//字头
    private String explanation;//
    private String volume;
    private String radical;//所属部
    private String pronunciation;
    private List<Variants> variants;
    private String seal_character;
    private String pinyin;
    private String pinyin_full;//拼音
    private List<String> components;
    private String xuan_note;
    private String kai_note;
    private List<DuanNotes> duan_notes;//段注
    private List<String> indexes;
}
