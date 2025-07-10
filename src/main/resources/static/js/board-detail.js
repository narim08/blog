document.addEventListener('DOMContentLoaded', () => {
    const username = localStorage.getItem('username');
    if (!username) {
        window.location.href = '/login.html';
        return;
    }

    const boardDetail = document.getElementById('board-detail');
    const backToListBtn = document.getElementById('back-to-list-btn');
    const editBoardBtn = document.getElementById('edit-board-btn');
    const deleteBoardBtn = document.getElementById('delete-board-btn');

    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get('id');
    let editingCommentId = null;


    async function fetchBoardDetails() {
        try {
            const response = await fetch(`/api/board/${boardId}`);
            const board = await response.json();

            boardDetail.innerHTML = `
                <h2>${board.title}</h2>
                <div style="display: flex; justify-content: space-between;">
                    <span class="from-content">from. ${board.userName}</span>
                    <span class="date-content">작성일: ${new Date(board.createTime).toLocaleString()}</span>
                </div>
                <div class="board-content">${board.content.replace(/\n/g, '<br>')}</div>

                <div class="board-interaction">
                    <div class="board-interaction-left">
                        <button id="like-btn">🤍 좋아요</button>
                        <span id="like-count">0</span>
                        <button id="share-btn">🔗 공유</button>
                    </div>
                    <div class="board-view-count">👁 조회수: ${board.viewCount}</div>
                </div>

                <div id="comments-section">
                    <h3>댓글</h3>
                    <div id="comments-list"></div>
                    <form id="comment-form">
                        <textarea id="comment-content" placeholder="댓글을 작성하세요" required></textarea>
                        <button type="submit">댓글 작성</button>
                    </form>
                </div>
            `;

            const shareButton = document.getElementById('share-btn');
            shareButton.addEventListener('click', () => {
                const currentUrl = window.location.href;
                navigator.clipboard.writeText(currentUrl);
                alert('📋 링크가 복사되었습니다!');
            });

            //게시글이 생성된 후, 좋아요 기능 추가됨
            const likeButton = document.getElementById('like-btn');
            const likeCountSpan = document.getElementById('like-count');

            async function updateLikeCount() {
                    const response = await fetch(`/api/board/${boardId}/like/count`);
                    const count = await response.json();
                    likeCountSpan.textContent = count;
                }

            async function toggleLike() {
                const response = await fetch(`/api/board/${boardId}/like`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Username': username }
                });
                const result = await response.json();
                likeButton.textContent = result.liked ? "❤️ 좋아요 취소" : "🤍 좋아요";
                likeCountSpan.textContent = result.likeCount;
            }

            likeButton.addEventListener('click', toggleLike);
            await updateLikeCount();


            //자기 글이 아니면 수정, 삭제 버튼 안 보임
            if (board.userName === username) {
                editBoardBtn.style.display = 'inline-block';
                deleteBoardBtn.style.display = 'inline-block';
            } else {
                editBoardBtn.style.display = 'none';
                deleteBoardBtn.style.display = 'none';
            }
            setupCommentForm();
            await fetchComments();
        } catch (error) {
            console.error('Error fetching board details:', error);
            alert('게시글 상세 정보를 불러오는 중 오류가 발생했습니다.');
        }
    }


    async function fetchComments() {
        const commentsList = document.getElementById('comments-list');

        try {
            const response = await fetch(`/api/board/${boardId}/comment`);
            const comments = await response.json();

            commentsList.innerHTML = comments.map(comment => `
                <div class="comment" data-comment-id="${comment.id}">
                    <div class="comment-meta">
                        <strong>${comment.userName}</strong>
                        <span>생성일: ${new Date(comment.createTime).toLocaleString()} | 수정일: ${new Date(comment.updateTime).toLocaleString()}</span>
                    </div>
                    <div class="comment-content">${comment.content}</div>
                    <div class="comment-actions">
                        ${comment.userName === username ? `
                            <button class="edit-comment-btn">수정</button>
                            <button class="delete-comment-btn">삭제</button>
                        ` : ''}
                    </div>
                </div>
            `).join('');

            setupCommentsEventListener();
        } catch (error) {
            console.error('Error fetching comments:', error);
        }
    }

    function setupCommentsEventListener() {
        const commentsList = document.getElementById('comments-list');
        commentsList.removeEventListener('click', handleCommentActions);
        commentsList.addEventListener('click', handleCommentActions);
    }

    function handleCommentActions(e) {
        const commentElement = e.target.closest('.comment');
        if (!commentElement) return;

        const commentId = commentElement.dataset.commentId;

        if (e.target.classList.contains('edit-comment-btn')) {
            startEditComment(commentElement);
        } else if (e.target.classList.contains('delete-comment-btn')) {
            deleteComment(commentId);
        }
    }

    function setupCommentForm() {
        const commentForm = document.getElementById('comment-form');
        const commentContentInput = document.getElementById('comment-content');
        const submitButton = commentForm.querySelector('button');

        commentForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const formData = { content: commentContentInput.value };

            try {
                let response;
                if (editingCommentId) {
                    response = await fetch(`/api/board/${boardId}/comment/${editingCommentId}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            'Username': username
                        },
                        body: JSON.stringify(formData)
                    });
                } else {
                    response = await fetch(`/api/board/${boardId}/comment`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Username': username
                        },
                        body: JSON.stringify(formData)
                    });
                }

                if (response.ok) {
                    await fetchComments();
                    commentForm.reset();
                    editingCommentId = null;
                    submitButton.textContent = '댓글 작성';
                } else {
                    alert('댓글 작성/수정 실패');
                }
            } catch (error) {
                console.error('Comment submit error:', error);
            }
        });
    }

    function startEditComment(commentElement) {
        const commentId = commentElement.dataset.commentId;
        const commentContent = commentElement.querySelector('.comment-content').innerText.trim();
        const contentInput = document.getElementById('comment-content');
        const submitButton = document.querySelector('#comment-form button');

        contentInput.value = commentContent;
        editingCommentId = commentId;
        submitButton.textContent = '댓글 수정';
    }

    async function deleteComment(commentId) {
        if (confirm('정말 삭제하시겠습니까?')) {
            try {
                const response = await fetch(`/api/board/${boardId}/comment/${commentId}`, {
                    method: 'DELETE',
                    headers: { 'Username': username }
                });

                if (response.ok) {
                    await fetchComments();
                } else {
                    alert('댓글 삭제 실패');
                }
            } catch (error) {
                console.error('Comment delete error:', error);
            }
        }
    }

    backToListBtn.addEventListener('click', () => {
        window.location.href = '/';
    });

    editBoardBtn.addEventListener('click', () => {
        window.location.href = `/edit-board.html?id=${boardId}`;
    });

    deleteBoardBtn.addEventListener('click', async () => {
        if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) return;

        try {
            const response = await fetch(`/api/board/${boardId}`, {
                method: 'DELETE',
                headers: { 'Username': username }
            });

            if (response.ok) {
                window.location.href = '/';
            } else {
                const errorData = await response.json();
                alert(errorData.message || '게시글 삭제에 실패했습니다.');
            }
        } catch (error) {
            console.error('Delete board error:', error);
            alert('게시글 삭제 중 오류가 발생했습니다.');
        }
    });

    fetchBoardDetails();
});
