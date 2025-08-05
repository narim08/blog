document.addEventListener('DOMContentLoaded', () => {

    let allBoards = [];  // 전체 게시글을 저장할 배열

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
            renderBoards(allBoards);  // 전체 게시글 렌더링
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

    // Function to render pagination
    function renderPagination(data) {
        paginationContainer.innerHTML = '';

        for (let i = 0; i < data.totalPages; i++) {
            const pageBtn = document.createElement('button');
            pageBtn.textContent = i + 1;
            pageBtn.addEventListener('click', () => fetchBoards(i, searchInput.value));
            paginationContainer.appendChild(pageBtn);
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

    // 태그 버튼 클릭 시 저장된 게시글에서 필터링
    const tagButtons = document.querySelectorAll('.tag');
    tagButtons.forEach(button => {
        button.addEventListener('click', () => {
            const clickedText = button.innerText.trim(); // 예: "태그1"

            // board.tag가 "tag-tag1" → 태그명을 "태그1"로 변환하여 비교
            const filtered = allBoards.filter(board => tagMap[board.tag] === clickedText);

            renderBoards(filtered); // 필터링된 게시글만 렌더링
            paginationContainer.innerHTML = ''; // 페이지네이션 제거
        });
    });

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
