<template>
	<div class="container">
		<div class="header">
			<h1 class="title">🛡️ WAF 网络安全防御系统</h1>
			<div class="btn-group">
				<button class="settings-btn" @click="goToRules">⚙️ 策略管理</button>
				<button class="refresh-btn" @click="fetchLogs">刷新数据</button>
			</div>
		</div>

		<div class="stats-panel">
			<div class="card danger">
				<h3>拦截总数</h3>
				<p class="number">{{ logs.length }}</p>
			</div>
			<div class="card warning">
				<h3>最近攻击IP</h3>
				<p class="ip">{{ logs.length > 0 ? logs[0].ipAddress : '无' }}</p>
			</div>
		</div>

		<div class="table-container">
			<table class="log-table">
				<thead>
					<tr>
						<th>ID</th>
						<th>攻击时间</th>
						<th>来源IP</th>
						<th>攻击类型</th>
						<th>请求路径</th>
						<th>Payload (攻击载荷)</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="(log, index) in logs" :key="log.id" :class="index % 2 === 0 ? 'even' : 'odd'">
						<td>{{ log.id }}</td>
						<td>{{ formatDate(log.createTime) }}</td>
						<td class="ip-cell">{{ log.ipAddress }}</td>
						<td>
							<span :class="['tag', getTagClass(log.attackType)]">{{ log.attackType }}</span>
						</td>
						<td>{{ log.requestUri }}</td>
						<td class="payload-cell" :title="log.payload">
							{{ truncate(log.payload) }}
						</td>
					</tr>
				</tbody>
			</table>
			
			<div v-if="logs.length === 0" class="empty-tip">
				暂无攻击记录，系统非常安全...
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted, onUnmounted } from 'vue';

	const logs = ref([]);
	let timer = null;

	// 跳转到规则管理页面
	const goToRules = () => {
		uni.navigateTo({
			url: '/pages/rules/rules'
		});
	};

	// 获取数据的函数
	const fetchLogs = () => {
		uni.request({
			url: 'http://localhost:8080/api/logs',
			method: 'GET',
			success: (res) => {
				logs.value = res.data;
			},
			fail: (err) => {
				console.error('获取失败:', err);
			}
		});
	};

	// 辅助函数：格式化时间
	const formatDate = (isoString) => {
		if (!isoString) return '';
		return isoString.replace('T', ' ').substring(0, 19);
	};

	// 辅助函数：截断字符串
	const truncate = (str) => {
		if (!str) return '';
		return str.length > 50 ? str.substring(0, 50) + '...' : str;
	};
	
	// 辅助函数：给标签上色
	const getTagClass = (type) => {
		if (!type) return '';
		if (type.includes('SQL')) return 'sql';
		if (type.includes('XSS')) return 'xss';
		if (type.includes('DDoS')) return 'ddos';
		return '';
	};

	// 生命周期：加载与销毁
	onMounted(() => {
		fetchLogs();
		// 开启3秒自动刷新
		timer = setInterval(fetchLogs, 3000);
	});

	onUnmounted(() => {
		if (timer) clearInterval(timer);
	});
</script>

<style>
	/* 全局背景和字体 */
	.container {
		padding: 20px;
		background-color: #f0f2f5;
		min-height: 100vh;
		font-family: 'Segoe UI', sans-serif;
	}

	/* 顶部标题栏 */
	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20px;
		background: white;
		padding: 15px 25px;
		border-radius: 8px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
	}
	
	.title {
		margin: 0;
		color: #1f2d3d;
		font-size: 24px;
	}
	
	.btn-group {
		display: flex;
		gap: 10px; /* 按钮之间的间距 */
	}

	.refresh-btn {
		background-color: #409eff;
		color: white;
		border: none;
		padding: 8px 20px;
		border-radius: 4px;
		cursor: pointer;
		font-weight: bold;
		font-size: 14px;
	}
	
	/* 新增：策略管理按钮样式 */
	.settings-btn {
		background-color: #67c23a; /* 绿色 */
		color: white;
		border: none;
		padding: 8px 20px;
		border-radius: 4px;
		cursor: pointer;
		font-weight: bold;
		font-size: 14px;
	}

	.refresh-btn:active, .settings-btn:active {
		opacity: 0.8;
	}

	/* 统计卡片 */
	.stats-panel {
		display: flex;
		gap: 20px;
		margin-bottom: 20px;
	}
	
	.card {
		background: white;
		padding: 20px;
		border-radius: 8px;
		flex: 1;
		box-shadow: 0 2px 8px rgba(0,0,0,0.05);
		text-align: center;
	}
	.card h3 { margin: 0 0 10px 0; color: #909399; font-size: 14px; }
	.card .number { font-size: 32px; font-weight: bold; margin: 0; }
	.card.danger .number { color: #f56c6c; }
	.card.warning .ip { color: #e6a23c; font-size: 24px; font-weight: bold; margin: 0;}

	/* 表格样式 */
	.table-container {
		background: white;
		padding: 20px;
		border-radius: 8px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
		overflow-x: auto;
	}

	.log-table {
		width: 100%;
		border-collapse: collapse;
		font-size: 14px;
	}

	.log-table th {
		background-color: #fafafa;
		color: #606266;
		font-weight: 600;
		text-align: left;
		padding: 12px;
		border-bottom: 1px solid #ebeef5;
	}

	.log-table td {
		padding: 12px;
		border-bottom: 1px solid #ebeef5;
		color: #606266;
	}

	.log-table tr:hover {
		background-color: #f5f7fa;
	}

	/* 标签样式 */
	.tag {
		padding: 4px 8px;
		border-radius: 4px;
		font-size: 12px;
		font-weight: bold;
	}
	.tag.sql { background-color: #fef0f0; color: #f56c6c; border: 1px solid #fde2e2; }
	.tag.xss { background-color: #fdf6ec; color: #e6a23c; border: 1px solid #faecd8; }
	.tag.ddos { background-color: #303133; color: #fff; border: 1px solid #000; }
	
	.payload-cell {
		font-family: Consolas, monospace;
		color: #888;
		max-width: 300px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.empty-tip {
		text-align: center;
		padding: 40px;
		color: #909399;
	}
</style>