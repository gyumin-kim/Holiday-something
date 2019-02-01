import React, { Component } from 'react';
import Header from '../components/Header/Header';
import MainWrapper from '../components/MainWrapper/MainWrapper';
import SideBar from '../components/SideBar/SideBar';
import MainContent from '../components/MainContent/MainContent';

class Mileage extends Component {
  state = {
    center: 'mileage',
  }

  // await을 쓰려면 바깥에 async가 있어야 한다
  _getUser = async () => {
    const user = await this._callApi();
    // await: 위 문장이 끝나기 전까지는(성공하든 실패하든) setState가 실행되지 않는다
    if (user === null) {
      console.log('user is null!');
    }
    this.setState({
      user: user,
    });
  };

  _callApi = () => {
    // 현재 로그인 되어 있는 user의 id를 가져와서 사용하는 것으로 변경해야 한다.
    // return fetch('http://localhost:8080/api/userTmp?id=11')
    return fetch('http://localhost:8080/api/user/authenticated')  // 현재 로그인된 유저
    .then(response => response.json())
    .then(json => json)
    .catch(err => console.error(err));
  };

  render() {
    return (
      <div>
        <Header />
        <MainWrapper>
          {this.state.user ? <SideBar user={this.state.user} /> : 'Loading ...'}

          {/* MainContent에 매번 다른 props를 넣어준다(Home, Orders, Account, Mileage...) */}
          {this.state.user ? 
            <MainContent 
              user={this.state.user} 
              mainCenter={this.state.center}  
            /> : 
          'Loading ...'}
        </MainWrapper>
      </div>
    );
  }

  componentDidMount() {
    this._getUser();
    if (this.state.user === null) {
      window.location.href = '/';
    }
  }
}

export default Mileage;