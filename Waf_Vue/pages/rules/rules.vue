<template>
	<div class="container">
		<div class="header">
			<h1 class="title">⚙️ 安全策略管理</h1>
			<button class="add-btn" @click="openDialog">＋ 添加规则</button>
		</div>

		<div class="table-container">
			<table class="rule-table">
				<thead>
					<tr>
						<th>ID</th>
						<th>规则名称</th>
						<th>类型</th>
						<th>正则表达式</th>
						<th>状态</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="rule in rules" :key="rule.id">
						<td>{{ rule.id }}</td>
						<td>{{ rule.ruleName }}</td>
						<td><span :class="['tag', rule.ruleType]">{{ rule.ruleType }}</span></td>
						<td class="code">{{ rule.regexPattern }}</td>
						<td>
							<switch :checked="rule.isActive === 1" @change="toggleStatus(rule)" />
						</td>
						<td>
							<button class="del-btn" @click="deleteRule(rule.id)">删除</button>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div v-if="showDialog" class="dialog-mask">
			<div class="dialog">
				<h3>添加新规则</h3>
				<input v-model="form.ruleName" placeholder="规则名称" />
				<select v-model="form.ruleType">
					<option value="SQL">SQL注入</option>
					<option value="XSS">XSS攻击</option>
				</select>
				<input v-model="form.regexPattern" placeholder="正则表达式 (例如: select|drop)" />
				<div class="btns">
					<button @click="saveRule">保存</button>
					<button @click="showDialog = false" class="cancel">取消</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
	import { ref, reactive, onMounted } from 'vue';

	const rules = ref([]);
	const showDialog = ref(false);
	const form = reactive({ ruleName: '', ruleType: 'SQL', regexPattern: '', isActive: 1 });

	const loadRules = () => {
		uni.request({
			url: 'http://localhost:8080/api/rules',
			success: (res) => rules.value = res.data
		});
	};

	const saveRule = () => {
		uni.request({
			url: 'http://localhost:8080/api/rules',
			method: 'POST',
			data: form,
			success: () => {
				showDialog.value = false;
				loadRules();
			}
		});
	};

	const deleteRule = (id) => {
		uni.request({
			url: 'http://localhost:8080/api/rules/' + id,
			method: 'DELETE',
			success: loadRules
		});
	};
    
    const toggleStatus = (rule) => {
        const newStatus = rule.isActive === 1 ? 0 : 1;
        uni.request({
            url: 'http://localhost:8080/api/rules',
            method: 'PUT',
            data: { ...rule, isActive: newStatus },
            success: loadRules
        });
    }

    const openDialog = () => {
        form.ruleName = '';
        form.regexPattern = '';
        showDialog.value = true;
    }

	onMounted(loadRules);
</script>

<style>
    /* 复用 index.vue 的部分样式 */
	.container { padding: 20px; background: #f0f2f5; min-height: 100vh; }
	.header { display: flex; justify-content: space-between; margin-bottom: 20px; background: white; padding: 15px; border-radius: 8px; }
    .table-container { background: white; padding: 20px; border-radius: 8px; }
	.rule-table { width: 100%; border-collapse: collapse; }
	.rule-table th, .rule-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: left; }
    .tag { padding: 2px 8px; border-radius: 4px; font-size: 12px; }
    .tag.SQL { background: #e8f3ff; color: #409eff; }
    .tag.XSS { background: #fdf6ec; color: #e6a23c; }
    .code { font-family: monospace; background: #f4f4f5; padding: 2px 5px; color: #c0392b; }
    
    .add-btn { background: #67c23a; color: white; border: none; padding: 8px 15px; border-radius: 4px; cursor: pointer; }
    .del-btn { background: #f56c6c; color: white; border: none; padding: 4px 10px; border-radius: 4px; cursor: pointer; }

    /* 弹窗样式 */
    .dialog-mask { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; }
    .dialog { background: white; padding: 20px; border-radius: 8px; width: 400px; display: flex; flex-direction: column; gap: 10px; }
    .dialog input, .dialog select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
    .btns { display: flex; gap: 10px; justify-content: flex-end; margin-top: 10px; }
    .btns button { padding: 8px 15px; border: none; border-radius: 4px; background: #409eff; color: white; cursor: pointer; }
    .btns .cancel { background: #909399; }
</style>