import {Avatar, Image, Space, Typography} from "antd";
import {UserOutlined} from "@ant-design/icons";
import Link from "next/link";
import UserMenu, {useUserMenu} from "@components/layouts/UserMenu";

const {Text} = Typography;

function NavBarText({text}) {
    return (
        <Text style={{color: 'white'}}>{text}</Text>
    )
}

export default function NavBar() {

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
                    <Link href="/">
                        <Image width={32} src="/ontrack-128.png" preview={false}/>
                    </Link>
                    <Link href="/">
                        <Text style={{color: "white", fontSize: '175%', verticalAlign: 'middle'}}>Ontrack</Text>
                    </Link>
                </Space>
                <Space direction="horizontal" size={8}>
                    <NavBarText text="Search component"/>
                    <NavBarText text="App messages"/>
                    <NavBarText text="User name"/>
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