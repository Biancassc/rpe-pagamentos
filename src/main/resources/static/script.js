
console.log("Script JavaScript carregado!");


function formatCpf(cpf) {
    if (!cpf) return '';
    const cleaned = cpf.replace(/\D/g, ''); 
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