package com.library.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.constant.Constants;
import com.library.common.dto.NoticeDTO;
import com.library.common.dto.OverdueMessage;
import com.library.common.dto.Result;
import com.library.common.entity.Notice;
import com.library.notice.mapper.NoticeMapper;
import com.library.notice.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public Result<Page<Notice>> listNotices(Integer page, Integer size, String type) {
        Page<Notice> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();

        // Only show announcements to all (not personal overdue notices)
        wrapper.eq(Notice::getType, type != null ? type : Constants.NOTICE_TYPE_ANNOUNCEMENT);
        wrapper.isNull(Notice::getTargetUserId); // public notices
        wrapper.orderByDesc(Notice::getCreateTime);

        noticeMapper.selectPage(pageParam, wrapper);
        return Result.success(pageParam);
    }

    @Override
    public Result<Page<Notice>> getMyNotices(Integer page, Integer size) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "Not logged in");
        }

        Page<Notice> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();

        // Public announcements OR personal notices for this user
        wrapper.and(w -> w.isNull(Notice::getTargetUserId)
                .or().eq(Notice::getTargetUserId, userId));
        wrapper.orderByDesc(Notice::getCreateTime);

        noticeMapper.selectPage(pageParam, wrapper);
        return Result.success(pageParam);
    }

    @Override
    public Result<Notice> getNoticeById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            return Result.error(404, "Notice not found");
        }

        // Mark as read
        if (notice.getIsRead() == 0) {
            notice.setIsRead(1);
            noticeMapper.updateById(notice);
        }

        return Result.success(notice);
    }

    @Override
    public Result<String> createNotice(NoticeDTO noticeDTO) {
        if (noticeDTO.getTitle() == null || noticeDTO.getTitle().trim().isEmpty()) {
            return Result.error(400, "Notice title must not be empty");
        }

        Notice notice = new Notice();
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent() != null ? noticeDTO.getContent() : "");
        notice.setType(noticeDTO.getType() != null ? noticeDTO.getType() : Constants.NOTICE_TYPE_ANNOUNCEMENT);
        notice.setTargetUserId(noticeDTO.getTargetUserId());
        notice.setIsRead(0);
        notice.setCreateTime(LocalDateTime.now());

        noticeMapper.insert(notice);
        log.info("Notice created: {}, type: {}", notice.getTitle(), notice.getType());
        return Result.success("Notice created successfully");
    }

    @Override
    public Result<String> deleteNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            return Result.error(404, "Notice not found");
        }
        noticeMapper.deleteById(id);
        return Result.success("Notice deleted successfully");
    }

    /**
     * Handle overdue message from RabbitMQ (Spring Cloud Stream)
     */
    @Override
    public void handleOverdueMessage(OverdueMessage message) {
        log.info("Received overdue message: userId={}, bookId={}, bookTitle={}",
                message.getUserId(), message.getBookId(), message.getBookTitle());

        Notice notice = new Notice();
        notice.setTitle("Book Overdue Reminder");
        notice.setContent(String.format(
                "Dear reader,\n\nThe book \"%s\" you borrowed was due on %s.\n" +
                "Please return it to the library as soon as possible.",
                message.getBookTitle() != null ? message.getBookTitle() : "Unknown",
                message.getDueTime() != null ? message.getDueTime() : "Unknown date"
        ));
        notice.setType(Constants.NOTICE_TYPE_OVERDUE);
        notice.setTargetUserId(message.getUserId());
        notice.setIsRead(0);
        notice.setCreateTime(LocalDateTime.now());

        noticeMapper.insert(notice);
        log.info("Overdue notice created for user: {}", message.getUserId());
    }

    private Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        String userIdHeader = request.getHeader(Constants.HEADER_USER_ID);
        if (userIdHeader != null) {
            return Long.valueOf(userIdHeader);
        }
        return null;
    }
}
