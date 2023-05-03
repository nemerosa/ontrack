import {Avatar, Space, Typography} from "antd";
import {UserOutlined} from "@ant-design/icons";
import UserMenu, {useUserMenu} from "@components/layouts/UserMenu";
import {homeLink} from "@components/common/Links";
import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";
import Image from 'next/image';

const {Text} = Typography;

function NavBarText({text}) {
    return (
        <Text style={{color: 'white'}}>{text}</Text>
    )
}

export default function NavBar() {

    const user = useContext(UserContext);
    const userMenu = useUserMenu();

    const openUserMenu = () => {
        userMenu.setOpen(true)
    }

    return (
        <>
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
            }}>
                <Space direction="horizontal" size={16}>
                    {
                        homeLink(<Image alt="Logo Ontrack" width={32} src="/ontrack-128.png"/>)
                    }
                    {
                        homeLink(<Text
                            style={{color: "white", fontSize: '175%', verticalAlign: 'middle'}}>Ontrack</Text>)
                    }
                </Space>
                <Space direction="horizontal" size={8}>
                    {/* TODO <NavBarText text="Search component"/>*/}
                    {/* TODO <NavBarText text="App messages"/>*/}
                    <NavBarText text={user?.account?.fullName}/>
                    <Avatar icon={<UserOutlined/>}
                            onClick={openUserMenu}
                            style={{
                                backgroundColor: 'white',
                                color: 'black',
                                cursor: 'pointer',
                            }}
                    />
                </Space>
            </div>

            <UserMenu userMenu={userMenu}/>
        </>
    )
}