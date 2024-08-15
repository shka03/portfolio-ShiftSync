// クライアント側で日時表示の更新を行う。
function updateDateTime() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); // 月は0から始まるため +1
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    const formattedDate = `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
    document.getElementById('currentDateTime').textContent = formattedDate;
}

// ページが読み込まれたときに日時を表示し、1秒ごとに更新
window.onload = function() {
    updateDateTime();
    setInterval(updateDateTime, 1000); // 1秒ごとにupdateDateTimeを呼び出す
};
