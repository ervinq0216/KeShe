<template>
	<div class="container">
		<div class="header">
			<h1 class="title">🛡️ WAF 网络安全防御系统 - 监控台</h1>
			<button class="refresh-btn" @click="fetchLogs">刷新数据</button>
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
							<span class="tag">{{ log.attackType }}</span>
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
	import { ref, onMounted } from 'vue';

	const logs = ref([]);

	// 获取数据的函数
	const fetchLogs = () => {
		uni.request({
			url: 'http://localhost:8080/api/logs', // 后端接口地址
			method: 'GET',
			success: (res) => {
				console.log('获取数据成功:', res.data);
				logs.value = res.data;
			},
			fail: (err) => {
				console.error('获取失败:', err);
				uni.showToast({
					title: '无法连接后端服务',
					icon: 'none'
				});
			}
		});
	};

	// 格式化时间 (简单的字符串处理)
	const formatDate = (isoString) => {
		if (!isoString) return '';
		return isoString.replace('T', ' ').substring(0, 19);
	};

	// 截断过长的 Payload
	const truncate = (str) => {
		if (!str) return '';
		return str.length > 50 ? str.substring(0, 50) + '...' : str;
	};

	// 页面加载时自动获取数据
	onMounted(() => {
		fetchLogs();
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

	.refresh-btn {
		background-color: #409eff;
		color: white;
		border: none;
		padding: 10px 20px;
		border-radius: 4px;
		cursor: pointer;
		font-weight: bold;
	}
	.refresh-btn:active {
		background-color: #337ecc;
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

	/* 特定列样式 */
	.tag {
		background-color: #fef0f0;
		color: #f56c6c;
		padding: 4px 8px;
		border-radius: 4px;
		font-size: 12px;
		border: 1px solid #fde2e2;
	}
	
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