<template>
  <div class="content">
    <a-spin :spinning="loading">
      <a-row :gutter="[16, 16]">
        <!-- 搜索行 -->
        <a-col :span="24">
          <div class="title-line">
            <span class="title">
              <a-typography-title :level="5" style="margin-bottom: 0">{{ $t("projectManage.topLine.title") }}</a-typography-title>
            </span>
            <a-input-search :placeholder="$t('projectManage.topLine.searchBar.searchInputPlaceholder')" :allowClear="true" style="width: 300px" @search="handleOnSearch">
              <template #enterButton>
                <a-button type="primary">
                  <template #icon> <icon-plusOutlined /></template>
                  {{ $t("projectManage.topLine.searchBar.searchButtonText") }}
                </a-button>
              </template>
            </a-input-search>
          </div>
        </a-col>
        <!-- 创建卡片 -->
        <a-col :span="6"> <project-create-card @action="handleCreateCardAction" /> </a-col>
        <!-- 视图卡片 -->
        <a-col :span="6" v-for="item in projectListData" :key="item.id">
          <project-view-card @delete="handleOnDelteProject" @edit="handleOnEditProject" :name="item.name" :describe="item.describe" :id="item.id" :tags="item.tags" />
        </a-col>
        <!-- 分页行 -->
        <a-col :span="24">
          <div class="pagination-line">
            <a-pagination v-model:current="pageCfg.current" v-model:pageSize="pageCfg.pageSize" :total="projectList.lenght" show-less-items />
          </div>
        </a-col>
      </a-row>
    </a-spin>
    <!-- 创建表单 -->
    <edit-modal v-model:visible="modalCfg.visible" :mode="modalCfg.mode" :id="modalCfg.id" @finish="handleModalFinish" />
  </div>
</template>

<script>
import { useI18n } from "@fesjs/fes";
import { PlusOutlined } from "@ant-design/icons-vue";
import ProjectCreateCard from "./components/projectCreateCard.vue";
import ProjectViewCard from "./components/projectViewCard.vue";
import EditModal from "./components/editModal.vue";
import { getProjectList, deleteProject } from "@/common/service";
export default {
  components: {
    ProjectViewCard,
    ProjectCreateCard,
    EditModal,
    iconPlusOutlined: PlusOutlined,
  },
  data() {
    return {
      // 弹窗参数
      modalCfg: {
        mode: "",
        id: "",
        visible: false,
      },
      // 项目列表
      projectList: [],
      // 是否加载中
      loading: false,
      // 分页配置
      pageCfg: {
        current: 1,
        pageSize: 11,
      },
    };
  },
  computed: {
    projectListData() {
      let strIndex = Math.max(this.pageCfg.current - 1, 0) * this.pageCfg.pageSize;
      return this.projectList.slice(strIndex, strIndex + this.pageCfg.pageSize);
    },
  },
  methods: {
    async getDataList(name) {
      this.loading = true;
      let { list } = await getProjectList(name);
      this.loading = false;
      this.projectList = list
        .map((item) => ({
          id: item.id,
          name: item.name,
          describe: item.description,
          tags: item.tags.split(","),
        }))
        .reverse();
    },
    // 模态框操作完成
    handleModalFinish() {
      this.getDataList();
    },
    // 新建卡片点击
    handleCreateCardAction() {
      this.modalCfg = {
        visible: true,
        mode: "create",
      };
    },
    // 处理搜索
    handleOnSearch(value) {
      this.pageCfg.current = 1;
      this.getDataList(value);
    },
    // 删除项目
    async handleOnDelteProject(id) {
      await deleteProject(id);
      this.getDataList();
    },
    // 编辑项目
    handleOnEditProject(id) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        id: id,
      };
    },
  },
  async mounted() {
    this.getDataList();
  },
};
</script>
<style scoped lang="less">
.content {
  padding: 16px;
  box-sizing: border-box;
}
.title-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .title {
    border-left: 6px solid #1890ff;
    padding-left: 6px;
  }
}
.pagination-line {
  display: flex;
  justify-content: flex-end;
}
</style>
