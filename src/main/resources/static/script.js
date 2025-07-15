console.log("Script JavaScript carregado!");

const API_BASE_URL = 'http://localhost:8080';

const clientListSection = document.getElementById('client-list');
const clientsTableBody = document.querySelector('#clients-table tbody');
const loadingClientsMsg = document.getElementById('loading-clients');
const noClientsMsg = document.getElementById('no-clients');

const invoiceDetailsSection = document.getElementById('invoice-details');
const clientNameInvoiceSpan = document.getElementById('client-name-invoice');
const invoicesTableBody = document.querySelector('#invoices-table tbody');
const loadingInvoicesMsg = document.getElementById('loading-invoices');
const noInvoicesMsg = document.getElementById('no-invoices');
const backToClientsButton = document.getElementById('back-to-clients');

const loadingOverlay = document.getElementById('loading-overlay');
const addClientForm = document.getElementById('add-client-form');
const clientNameInput = document.getElementById('client-name');
const clientCpfInput = document.getElementById('client-cpf');
const clientDobInput = document.getElementById('client-dob');
const clientLimitInput = document.getElementById('client-limit');
const formMessageDiv = document.getElementById('form-message');

clientCpfInput.addEventListener('input', function (e) {
    let value = e.target.value.replace(/\D/g, '');
    let formattedValue = '';

    if (value.length > 0) {
        formattedValue += value.substring(0, 3);
        if (value.length > 3) {
            formattedValue += '.' + value.substring(3, 6);
        }
        if (value.length > 6) {
            formattedValue += '.' + value.substring(6, 9);
        }
        if (value.length > 9) {
            formattedValue += '-' + value.substring(9, 11);
        }
    }
    e.target.value = formattedValue;
});

function formatCpf(cpf) {
    if (!cpf) return '';
    const cleaned = String(cpf).replace(/\D/g, '');
    return cleaned.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function calculateAge(dobString) {
    if (!dobString) return '';
    const dob = new Date(dobString);
    const today = new Date();
    let age = today.getFullYear() - dob.getFullYear();
    const m = today.getMonth() - dob.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
        age--;
    }
    return age;
}

function showLoading() {
    loadingOverlay.classList.add('visible');
}

function hideLoading() {
    loadingOverlay.classList.remove('visible');
}

function showFormMessage(message, isSuccess = true) {
    formMessageDiv.textContent = message;
    formMessageDiv.className = isSuccess ? 'success' : 'error';
}

function clearForm() {
    addClientForm.reset();
    formMessageDiv.textContent = '';
    formMessageDiv.className = '';
}

async function addClient(event) {
    event.preventDefault();

    showLoading();
    showFormMessage('');

    const clientData = {
        nome: clientNameInput.value,
        cpf: formatCpf(clientCpfInput.value),
        dataNascimento: clientDobInput.value,
        limiteCredito: parseFloat(clientLimitInput.value)
    };

    try {
        const response = await fetch(`${API_BASE_URL}/clientes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(clientData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `Erro ao cadastrar! Status: ${response.status}`);
        }

        const newClient = await response.json();
        console.log('Cliente cadastrado:', newClient);
        showFormMessage('Cliente cadastrado com sucesso!', true);
        clearForm();
        await fetchClients();
    } catch (error) {
        console.error('Erro ao cadastrar cliente:', error);
        showFormMessage(`Falha no cadastro: ${error.message}`, false);
    } finally {
        hideLoading();
    }
}

async function fetchClients() {
    showLoading();
    loadingClientsMsg.style.display = 'block';
    noClientsMsg.style.display = 'none';
    clientsTableBody.innerHTML = '';

    try {
        const response = await fetch(`${API_BASE_URL}/clientes`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const clients = await response.json();

        if (clients.length === 0) {
            noClientsMsg.style.display = 'block';
        } else {
            clients.forEach(client => {
                const row = clientsTableBody.insertRow();
                row.insertCell().textContent = client.nome;
                row.insertCell().textContent = formatCpf(client.cpf);
                row.insertCell().textContent = calculateAge(client.dataNascimento);
                row.insertCell().textContent = client.statusBloqueio === 'A' ? 'Ativo' : 'Bloqueado';
                row.insertCell().textContent = `R$ ${client.limiteCredito.toFixed(2)}`;

                const actionsCell = row.insertCell();
                const viewInvoicesButton = document.createElement('button');
                viewInvoicesButton.textContent = 'Ver Faturas';
                viewInvoicesButton.onclick = () => showInvoices(client.id, client.nome);
                actionsCell.appendChild(viewInvoicesButton);
            });
        }
    } catch (error) {
        console.error('Erro ao buscar clientes:', error);
        noClientsMsg.textContent = 'Erro ao carregar clientes. Tente novamente mais tarde.';
        noClientsMsg.style.display = 'block';
    } finally {
        loadingClientsMsg.style.display = 'none';
        hideLoading();
    }
}

async function showInvoices(clientId, clientName) {
    showLoading();
    clientNameInvoiceSpan.textContent = clientName;
    clientListSection.style.display = 'none';
    invoiceDetailsSection.style.display = 'block';

    backToClientsButton.dataset.clientId = clientId;

    loadingInvoicesMsg.style.display = 'block';
    noInvoicesMsg.style.display = 'none';
    invoicesTableBody.innerHTML = '';

    try {
        const response = await fetch(`${API_BASE_URL}/faturas/cliente/${clientId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const invoices = await response.json();

        if (invoices.length === 0) {
            noInvoicesMsg.textContent = 'Nenhuma fatura encontrada para este cliente.';
            noInvoicesMsg.style.display = 'block';
        } else {
            invoices.forEach(invoice => {
                const row = invoicesTableBody.insertRow();
                row.insertCell().textContent = `R$ ${invoice.valor.toFixed(2)}`;
                row.insertCell().textContent = new Date(invoice.dataVencimento).toLocaleDateString('pt-BR');
                let statusText;
                switch (invoice.status) {
                    case 'P': statusText = 'Paga'; break;
                    case 'A': statusText = 'Atrasada'; break;
                    case 'B': statusText = 'Aberta'; break;
                    default: statusText = 'Desconhecido';
                }
                row.insertCell().textContent = statusText;
                row.insertCell().textContent = invoice.dataPagamento ? new Date(invoice.dataPagamento).toLocaleDateString('pt-BR') : '-';

                const actionsCell = row.insertCell();
                const payButton = document.createElement('button');
                payButton.textContent = 'Registrar Pagamento';
                payButton.classList.add('payment-button');

                if (invoice.status === 'P') {
                    payButton.disabled = true;
                    payButton.classList.add('disabled-button');
                    payButton.textContent = 'Já Paga';
                } else {
                    payButton.onclick = () => registerPayment(invoice.id);
                }
                actionsCell.appendChild(payButton);
            });
        }
    } catch (error) {
        console.error('Erro ao buscar faturas:', error);
        noInvoicesMsg.textContent = 'Erro ao carregar faturas. Tente novamente mais tarde.';
        noInvoicesMsg.style.display = 'block';
    } finally {
        loadingInvoicesMsg.style.display = 'none';
        hideLoading();
    }
}

async function registerPayment(invoiceId) {
    if (confirm(`Tem certeza que deseja registrar o pagamento da fatura ID: ${invoiceId}?`)) {
        showLoading();
        try {
            const response = await fetch(`${API_BASE_URL}/faturas/${invoiceId}/pagamento`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `Erro ao registrar pagamento! Status: ${response.status}`);
            }

            alert(`Pagamento da fatura ${invoiceId} registrado com sucesso!`);
            const currentClientId = backToClientsButton.dataset.clientId;
            const currentClientName = clientNameInvoiceSpan.textContent;
            await showInvoices(currentClientId, currentClientName);
        } catch (error) {
            console.error('Erro ao registrar pagamento:', error);
            alert(`Falha ao registrar pagamento: ${error.message}`);
        } finally {
            hideLoading();
        }
    }
}

backToClientsButton.onclick = () => {
    invoiceDetailsSection.style.display = 'none';
    clientListSection.style.display = 'block';
    fetchClients();
};

document.addEventListener('DOMContentLoaded', fetchClients);

addClientForm.addEventListener('submit', addClient);