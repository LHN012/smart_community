const handleLogin = async () => {
    try {
        const response = await axios.post('/auth/login', {
            username: loginForm.username,
            password: loginForm.password
        });
        
        if (response.data.code === 200) {
            const { token, user } = response.data.data;
            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify(user));
            ElMessage.success('登录成功');
            router.push('/admin');
        } else {
            ElMessage.error(response.data.msg || '登录失败');
        }
    } catch (error) {
        console.error('登录失败:', error);
        ElMessage.error(error.response?.data?.msg || '登录失败，请稍后重试');
    }
}; 