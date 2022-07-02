package top.panll.assist.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;

import java.util.List;

public class CustomPage<T> {

    /**
     * 默认分页彩虹展示数量
     */
    public static final int RAINBOW_NUM = 5;

    /**
     * 第几页
     */
    private Integer pageNo = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 20;

    /**
     * 总页数
     */
    private Integer totalPage = 0;

    /**
     * 总记录数
     */
    private Integer totalRows = 0;

    /**
     * 结果集
     */
    private List<T> rows;

    /**
     * 分页彩虹
     */
    private int[] rainbow;

    public CustomPage() {

    }

    public CustomPage(Integer pageSize, Integer pageNo, Integer totalPage, Integer totalRows, List<T> data) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.totalPage = totalPage;
        this.totalRows = totalRows;
        this.rows = data;
    }

    public CustomPage(PageInfo<T> pageInfo) {
        this.setRows(pageInfo.getList());
        this.setTotalRows(Convert.toInt(pageInfo.getTotal()));
        this.setPageNo(pageInfo.getPageNum());
        this.setPageSize(pageInfo.getPageSize());
        this.setTotalPage(pageInfo.getPages());
        this.setRainbow(PageUtil.rainbow(pageInfo.getSize(), pageInfo.getPages(), RAINBOW_NUM));
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int[] getRainbow() {
        return rainbow;
    }

    public void setRainbow(int[] rainbow) {
        this.rainbow = rainbow;
    }
}
