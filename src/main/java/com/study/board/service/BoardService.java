package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    // 글 작성 처리
    public void write(Board board, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {

            // 파일을 저장할 경로 지정
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // UUID -> 시스템 내에서 중복되지 않는 고유한 '식별자'를 생성하는 데 사용되는 128비트 길이의 값
            // 파일 이름을 랜덤으로 생성해줌
            // 랜덤으로 생성된 이름과 매개변수로 들어오는 file의 이름을 합친다.
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // File Class를 이용해 매개변수로 들어오는 file을 담을 빈 껍데기 파일 생성
            // dir: 파일 경로, fileName: 파일 이름
            File saveFile = new File(dir, fileName);

            // 알맹이를 껍데기에 넣어줌
            file.transferTo(saveFile);

            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName); // ← 브라우저에서 바로 접근 가능
        }

        // 파일이 비어있으면 기존 파일 유지 (수정 로직일 때)
        boardRepository.save(board);
    }

    // 게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable) {

        return boardRepository.findAll(pageable);
    }

    // 게시글 리스트 검색 기능 처리
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {

        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 특정 게시글 불러오기
    public Board boardView(Integer id) {

        return boardRepository.findById(id).get();
    }

    // 특정 게시글 삭제
    public void boardDelete(Integer id) {

        boardRepository.deleteById(id);
    }
}
