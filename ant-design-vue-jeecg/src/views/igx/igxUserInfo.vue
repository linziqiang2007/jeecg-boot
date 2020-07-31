<template>
  <a-card :bordered="false" style="height:100%;padding-bottom:200px; ">
<!--    <div class="igxUserInfo">-->
<!--        <pre v-text="$attrs"/>-->
<!--    </div>-->
  <div class="table-page-search-wrapper">
    <a-form layout="inline" :form="form">


      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="用户code">
            <a-col :span="12">{{ code}}</a-col>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="用户id">
            <a-col :span="12">{{ userInfo.loginAccount}}</a-col>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="用户姓名">
            <a-col :span="12">{{ userInfo.name}}</a-col>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="用户身份证号码">
            <a-col :span="12">{{ userInfo.idCardNo}}</a-col>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="用户电话">
            <a-col :span="12">{{ userInfo.mobile}}</a-col>
          </a-form-item>
        </a-col>
      </a-row>

    </a-form>
  </div>
  </a-card>
</template>

<script>
    import { hybridBridge } from '@tanbo/hybrid-bridge';
    import { getAction, } from '@/api/manage'

    export default {
        name: 'igxUserInfo',
        inject: ['closeCurrent'],
        components: {
        },
        data() {
            return {
                form: this.$form.createForm(this),
                code: "",
                clientId: "lhC8ikdp0BKDugdzDGob",
                clientSecret:"4xXnsun7iCfbLCQopBQI",
                token:"",
                userInfo:{
                    name:"AAAA",
                    mobile:"180000000",
                    idCardNo:"****",
                    loginAccount:"8888888"
                },
            }
        },
        computed: {

        },
        created (){
            this.init();
        },
        methods: {
            init() {
                // hybridBridge.location.get().then(result => {
                //     console.log(result);
                // });
                this.getUserInfo();
            },
            //获取定位
            getUserLocaction() {
                hybridBridge.device.getLocation().then(function (location) {
                    console.log(location);
                    // location = {
                    //   /** 经度 */
                    //   longitude: string;
                    //   /** 纬度 */
                    //   latitude: string;
                    //   /** 详细地址 */
                    //   detailAddress: string;
                    //   /** 城市 */
                    //   cityName: string;
                    //   /** 区域名称 */
                    //   region: string;
                    // }
                })
            },
            //获取token
            getTokenByCode() {
                getAction("/aigx/IgxBaseApi/getIgxToken", params).then((res) => {
                    if(res.success){
                        this.token = res.message;
                        console.log(this.token);
                        this.getUserInfoAction();
                    }else{
                        message.error("根据code获取token失败！")
                    }
                });
            },
            //获取用户信息
            getUserInfo() {
                this.getUserShouquan();
                // this.getTokenByCode();

            },

            getUserInfoAction(){
                getAction("/aigx/IgxBaseApi/getIgxUserInfo", params).then((res) => {
                    if(res.success){
                        this.userInfo = res.message
                    }else{
                        message.error("获取用户信息失败！")
                    }
                });
            },

            //用户信息授权
            getUserShouquan() {
                var clientID = this.clientId;
                hybridBridge.user.authorization(clientID).then(function (result) {
                    message.info("result code:"+result);
                    // result = {
                    //   state: string,
                    //   code: string
                    // };
                    this.code = result.code;
                    message.info("code:" + this.code)
                    this.getTokenByCode(this.code);
                })
            },
        },
    }
</script>

<style lang="less" scoped>
  .ant-card-body .table-operator {
    margin-bottom: 18px;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 15px;
    padding-bottom: 15px;
  }

  .anty-row-operator button {
    margin: 0 5px
  }

  .ant-btn-danger {
    background-color: #ffffff
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }
</style>
