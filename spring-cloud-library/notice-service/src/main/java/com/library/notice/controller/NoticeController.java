package com.library.notice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.dto.NoticeDTO;
import com.library.common.dto.Result;
import com.library.common.entity.Notice;
import com.library.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * List public announcements
     */
    @GetMapping("/list")
    public Result<Page<Notice>> listNotices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type) {
        return noticeService.listNotices(page, size, type);
    }

    /**
     * Get current user's notices (personal + public)
     */
    @GetMapping("/my")
    public Result<Page<Notice>> getMyNotices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return noticeService.getMyNotices(page, size);
    }

    /**
     * Get notice detail
     */
    @GetMapping("/{id}")
    public Result<Notice> getNoticeById(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

    /**
     * Create a notice (admin only via gateway)
     */
    @PostMapping
    public Result<String> createNotice(@RequestBody NoticeDTO noticeDTO) {
        return noticeService.createNotice(noticeDTO);
    }

    /**
     * Delete a notice (admin only via gateway)
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteNotice(@PathVariable Long id) {
        return noticeService.deleteNotice(id);
    }
}
