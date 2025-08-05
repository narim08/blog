document.addEventListener('DOMContentLoaded', () => {

    let allBoards = [];  // 전체 게시글을 저장할 배열
    let filteredBoards = [];  // 필터링된 게시글 배열
    let isFiltering = false;  // 필터링 상태 플래그

    function timeAgo(timeString) {
        const now = new Date();
        const past = new Date(timeString);
        const diff = Math.floor((now - past) / 1000);

        if (diff < 60) return `${diff}초 전`;
        if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
        if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
        if (diff < 604800) return `${Math.floor(diff / 86400)}일 전`;

        return past.toLocaleDateString();
    }

    const createBoardBtn = document.getElementById('create-board-btn');
    const boardList = document.getElementById('board-list');
    const paginationContainer = document.getElementById('pagination');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');

    // Check authentication
    const username = localStorage.getItem('username');
    if (!username) {
        window.location.href = '/login.html';
        return;
    }

    const profileIcon = document.getElementById('profile-icon');
    profileIcon.addEventListener('click', () => {
        window.location.href = '/mypage.html';
    });

    // 로그인한 사용자의 프로필 사진 설정
    const userProfile = localStorage.getItem('profileImage');
    if (userProfile) {
        profileIcon.src = userProfile;
    }

    // 로그아웃 버튼 기능 추가
    const logoutBtn = document.getElementById('logout-btn');
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('username');
        window.location.href = '/login.html';
    });

    // Function to fetch board list
    async function fetchBoards(page = 0, searchTitle = '') {
        try {
            let url = `/api/board?page=${page}&size=5&sort=createTime,desc`;
            if (searchTitle) {
                url = `/api/board/search?title=${encodeURIComponent(searchTitle)}&page=${page}&size=5&sort=createTime,desc`;
            }

            const response = await fetch(url);
            const data = await response.json();

            allBoards = data.content; // 전체 게시글을 저장
            isFiltering = false; // 필터링 아님
            renderBoards(allBoards);
            renderPagination(data);
        } catch (error) {
            console.error('Error fetching boards:', error);
            alert('게시글을 불러오는 중 오류가 발생했습니다.');
        }
    }

    // 게시글 렌더링 함수
    function renderBoards(boards) {
        boardList.innerHTML = '';

        boards.forEach(board => {
            const boardItem = document.createElement('div');
            boardItem.classList.add('board-item');
            boardItem.innerHTML = `
                <div class="board-user-info">
                    <img src="/images/default-profile.png" alt="프로필 사진" class="profile-image" />
                    <span class="user-name">${board.userName}</span>
                    <div class="board-tag ${board.tag}"></div>
                </div>
                <div class="board-content">
                    <h3 class="board-title">${board.title}</h3>
                    <p class="board-preview">${board.content}</p>
                    <div class="board-meta">
                        <span class="likes">❤️ ${board.likes || 0}</span>
                        <span class="time" title="${new Date(board.createTime).toLocaleString()}">
                            ${timeAgo(board.createTime)}
                        </span>
                    </div>
                </div>
            `;
            boardItem.addEventListener('click', () => {
                window.location.href = `/board-detail.html?id=${board.id}`;
            });
            boardList.appendChild(boardItem);
        });
    }

    // Function to render pagination (기본 서버 페이징용)
    function renderPagination(data) {
        paginationContainer.innerHTML = '';

        for (let i = 0; i < data.totalPages; i++) {
            const pageBtn = document.createElement('button');
            pageBtn.textContent = i + 1;
            pageBtn.addEventListener('click', () => fetchBoards(i, searchInput.value));
            paginationContainer.appendChild(pageBtn);
        }
    }

    // 클라이언트 페이징 (필터링 결과용)
    function renderBoardsWithPagination(boards, page = 0) {
        const pageSize = 5;
        const start = page * pageSize;
        const end = start + pageSize;
        const paginatedBoards = boards.slice(start, end);

        renderBoards(paginatedBoards);

        paginationContainer.innerHTML = '';
        const totalPages = Math.ceil(boards.length / pageSize);
        for (let i = 0; i < totalPages; i++) {
            const pageBtn = document.createElement('button');
            pageBtn.textContent = i + 1;
            pageBtn.addEventListener('click', () => renderBoardsWithPagination(boards, i));
            paginationContainer.appendChild(pageBtn);
        }
    }

    // 전체 게시글 중 태그 필터
    async function fetchBoardsForTagFilter(tagLabel) {
        try {
            const response = await fetch(`/api/board?page=0&size=9999&sort=createTime,desc`);
            const data = await response.json();

            allBoards = data.content;
            filteredBoards = allBoards.filter(board => tagMap[board.tag] === tagLabel);
            isFiltering = true;

            renderBoardsWithPagination(filteredBoards, 0);
        } catch (error) {
            console.error('태그 필터 게시글 로딩 중 오류:', error);
            alert('태그별 게시글을 불러오는 중 오류가 발생했습니다.');
        }
    }

    // Initial fetch
    fetchBoards();

    // Create board button
    createBoardBtn.addEventListener('click', () => {
        window.location.href = '/character-background.html';
    });

    // Search functionality
    searchBtn.addEventListener('click', () => {
        fetchBoards(0, searchInput.value);
    });

    const tagMap = {
        "tag-tag1": "태그1",
        "tag-tag2": "태그2",
        "tag-tag3": "태그3",
        "tag-tag4": "태그4",
        "tag-tag5": "태그5",
        "tag-tag6": "태그6"
    };

    // 태그 버튼 클릭 시 전체 데이터에서 필터링
    const tagButtons = document.querySelectorAll('.tag');
    tagButtons.forEach(button => {
        button.addEventListener('click', () => {
            const clickedText = button.innerText.trim();
            fetchBoardsForTagFilter(clickedText);
        });
    });
ㄹ
    /*
    // Add a logout button
    const logoutBtn = document.createElement('button');
    logoutBtn.textContent = '로그아웃';
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('username');
        window.location.href = '/login.html';
    });
    */
    /*
    document.querySelector('.container').insertBefore(logoutBtn, createBoardBtn);
    */
});
