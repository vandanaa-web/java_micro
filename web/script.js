document.addEventListener('DOMContentLoaded', () => {
    const terminal = document.getElementById('terminal');
    const commandForm = document.getElementById('commandForm');
    const commandInput = document.getElementById('commandInput');
    const sendBtn = document.getElementById('sendBtn');
    const roomNameEl = document.getElementById('roomName');
    const movesLeftEl = document.getElementById('movesLeft');
    const inventoryListEl = document.getElementById('inventoryList');

    const bannerModal = document.getElementById('bannerModal');
    const bannerTitle = document.getElementById('bannerTitle');
    const bannerMessage = document.getElementById('bannerMessage');
    const modalRestartBtn = document.getElementById('modalRestartBtn');

    // Controls list for enable/disable toggle
    const interactiveControls = [
        commandInput, sendBtn,
        document.getElementById('btnNorth'),
        document.getElementById('btnSouth'),
        document.getElementById('btnEast'),
        document.getElementById('btnWest'),
        document.getElementById('btnLook'),
        document.getElementById('btnTake'),
        document.getElementById('btnRead'),
        document.getElementById('btnHint'),
        document.getElementById('btnHelp')
    ];

    // Fetch initial state
    fetchState();

    // Event Listeners
    commandForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const cmd = commandInput.value.trim();
        if (cmd) {
            sendCommand(cmd);
        }
    });

    document.getElementById('btnNorth').addEventListener('click', () => sendCommand('go north'));
    document.getElementById('btnSouth').addEventListener('click', () => sendCommand('go south'));
    document.getElementById('btnEast').addEventListener('click', () => sendCommand('go east'));
    document.getElementById('btnWest').addEventListener('click', () => sendCommand('go west'));

    document.getElementById('btnLook').addEventListener('click', () => sendCommand('look'));
    document.getElementById('btnHint').addEventListener('click', () => sendCommand('hint'));
    document.getElementById('btnHelp').addEventListener('click', () => sendCommand('help'));

    document.getElementById('btnTake').addEventListener('click', () => {
        const item = prompt('Enter item name to take:');
        if (item && item.trim()) {
            sendCommand('take ' + item.trim());
        }
    });

    document.getElementById('btnRead').addEventListener('click', () => {
        const item = prompt('Enter item name to read:');
        if (item && item.trim()) {
            sendCommand('read ' + item.trim());
        }
    });

    document.getElementById('btnRestart').addEventListener('click', restartGame);
    modalRestartBtn.addEventListener('click', restartGame);

    /**
     * Fetches current state from server API.
     */
    async function fetchState() {
        try {
            const res = await fetch('/api/state');
            const data = await res.json();
            terminal.textContent = data.output;
            updateUI(data);
        } catch (err) {
            console.error('Failed to fetch game state:', err);
        }
    }

    /**
     * Sends command string to server API.
     */
    async function sendCommand(cmdStr) {
        terminal.textContent += `\n> ${cmdStr}\n`;
        commandInput.value = '';

        try {
            const res = await fetch('/api/command', {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' },
                body: cmdStr
            });
            const data = await res.json();
            if (data.output) {
                terminal.textContent += data.output + '\n';
            }
            terminal.scrollTop = terminal.scrollHeight;
            updateUI(data);
        } catch (err) {
            console.error('Failed to send command:', err);
        }
    }

    /**
     * Restarts game session via server API.
     */
    async function restartGame() {
        try {
            const res = await fetch('/api/restart', { method: 'POST' });
            const data = await res.json();
            terminal.textContent = data.output;
            terminal.scrollTop = 0;
            updateUI(data);
        } catch (err) {
            console.error('Failed to restart game:', err);
        }
    }

    /**
     * Updates sidebar widgets and checks game-over condition.
     */
    function updateUI(data) {
        roomNameEl.textContent = data.room || 'Unknown';
        movesLeftEl.textContent = data.movesLeft !== undefined ? data.movesLeft : 30;

        // Inventory update
        inventoryListEl.innerHTML = '';
        if (!data.inventory || data.inventory.length === 0) {
            inventoryListEl.innerHTML = '<li class="empty-inv">(Empty)</li>';
        } else {
            data.inventory.forEach(item => {
                const li = document.createElement('li');
                li.textContent = item;
                inventoryListEl.appendChild(li);
            });
        }

        // Game over check
        if (data.gameOver) {
            interactiveControls.forEach(ctrl => ctrl.disabled = true);
            bannerModal.classList.remove('hidden');

            if (data.won) {
                bannerTitle.textContent = '🎉 VICTORY!';
                bannerMessage.textContent = 'You successfully escaped the Haunted Mansion!';
                bannerTitle.style.color = 'var(--accent-green)';
            } else {
                bannerTitle.textContent = '☠️ GAME OVER';
                bannerMessage.textContent = 'You ran out of moves and failed to escape.';
                bannerTitle.style.color = 'var(--accent-red)';
            }
        } else {
            interactiveControls.forEach(ctrl => ctrl.disabled = false);
            bannerModal.classList.add('hidden');
        }
    }
});
