package com.library.notice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.dto.NoticeDTO;
import com.library.common.dto.OverdueMessage;
import com.library.common.dto.Result;
import com.library.common.entity.Notice;

public interface NoticeService {
    Result<Page<Notice>> listNotices(Integer page, Integer size, String type);
    Result<Page<Notice>> getMyNotices(Integer page, Integer size);
    Result<Notice> getNoticeById(Long id);
    Result<String> createNotice(NoticeDTO noticeDTO);
    Result<String> deleteNotice(Long id);
    void handleOverdueMessage(OverdueMessage message);
}
